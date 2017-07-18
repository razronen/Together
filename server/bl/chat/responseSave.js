var mongoose = require('mongoose');
var user = mongoose.model('user');
var chatConversations = mongoose.model('chat_conversation');
var response = mongoose.model('response');
var chat_queue = mongoose.model('chat_queue')
var FCM = require('fcm-node');
var fcm = new FCM(require('../../package.json').server_key);
var time_statistics = mongoose.model('time_statistics');

/**
 * Save a response to conversation.
 * Saves responses to chat queues, sending it to peer, and
 * adjusting time statistics.
 * @param reqResponse - the response.
 * @param next
 */
exports.saveResponse = function(reqResponse, next) {

    response.find({child_id: reqResponse.child_id})
        .sort('num')
        .exec(function(err,data1){
            if(err) next(err)
            else {
                if (data1.length!=0) {
                    reqResponse.num = data1[data1.length -1].num+1;

                } else {
                    reqResponse.num = 1;
                }
                var newResponse = new response(reqResponse);
                saveResponseToChatQueues(reqResponse, function(err){
                    if(err) next(err);
                    else {
                        sendResponseToPeer(reqResponse, function(){
                            newResponse.save(function(err, data2){
                                if(err) next(err, data2);
                                else {
                                    saveToTimeStatistics(reqResponse.author_id,reqResponse.author_entity, reqResponse.author_name,reqResponse.num);
                                    next(err,data2);
                                }
                            });
                        })
                    }
                })
            }
        });
}

/**
 * Saving that their was a response submited in this time. - (for day statistics)
 * Saving whos entity submited this response.
 * @param _id - of user submittd.
 * @param _entity -of that user
 * @param _name - of that user
 * @param _num - of response in the conversation.
 */
var saveToTimeStatistics = function(_id,_entity,_name,_num){
    //saving chat_started
    if(_num==1){
        var chatStarted = {chat_started: (new Date).getTime()};
        var chatNewObject = time_statistics(chatStarted);
        chatNewObject.save();
    }

    //saving chat_responses
    var chatResponse = {chat_responses: (new Date).getTime()};
    var chatNewResponse = time_statistics(chatResponse);
    chatNewResponse.save();

    //saving action
    var action = undefined;
    if(_entity=='Child'){
        action = {child_action: {time : (new Date).getTime(), name: _name, id: _id}}
    } else{
        action = {psychologist_action: {time : (new Date).getTime(), name: _name, id: _id}}
    }
    var action_statistics = new time_statistics(action);
    action_statistics.save();
}

/**
 * Sending the message to peer.
 * Check the submitted entity and then sending the messages to it's peer conversation.
 * @param reqResponse
 * @param next
 */
var sendResponseToPeer = function(reqResponse, next){
    chatConversations.findOne({ child_id: reqResponse.child_id}, function(err, data){
        if(err || data==undefined) next(err);
        else {
            saveResponseToStatistics(reqResponse.author_id,reqResponse.author_name,  reqResponse.message);
            //send to child:
            if(reqResponse.author_id!=reqResponse.child_id){
                SendUpdatedMessage(reqResponse.author_name,data.child_deviceId, reqResponse.message, function(){
                    saveResponseToStatistics(reqResponse.child_id,reqResponse.author_name,  reqResponse.message);
                    next();
                });
            } else {
                //send to each psychologist:
                if(data.psycho.length!=0){
                    for(var i = 0; i < data.psycho.length; i++){
                        saveResponseToStatistics(data.psycho[i].id,reqResponse.author_name,  reqResponse.message);
                        SendUpdatedMessage(reqResponse.author_name,data.psycho[i].psycho_deviceId, reqResponse.message, function(){
                            next();
                        });
                    }
                } else {
                    next();
                }
            }
        }
    })
};

/**
 * Saving that a response was submmited in this time(for user statistics).
 * @param _id - of user.
 * @param _peer - to whom
 * @param message - content of message.
 */
var saveResponseToStatistics = function(_id,_peer, message){
    var conditions = {id: _id}
        ,options = {multi: true}
        ,update ={$push: {"conversations": {   peer: _peer,
        msg: message  } } };
    user.update(conditions, update, options, function (err, numAffected) {
    });
}

/**
 * Sending the message to device Id by Firebase.
 * @param author - message author.
 * @param _to - target device Id.
 * @param message - content of message
 * @param next - callback function.
 * @constructor
 */
var SendUpdatedMessage = function(author, _to, message, next){
    var message = {
        to: _to,
        collapse_key: 'AIzaSyB_xvM52ngkCFS_gzgnTzaQtJWz4dTe2ws',

        notification: {
            title: author,
            body: message
        },

        data: {  //you can send only notification or only data(or include both)
            my_key: author,
            my_another_key: message
        }
    }
    fcm.send(message, function(err, response){
        if (err) {
            next();
            console.log("Something has gone wrong!");
        } else {
            next();
            console.log("Successfully sent with response: ", response);
        }
    });
}

/**
 * Saving the respons to chat queues.
 * So later, the psychologist could choose to whom to talk to by their last response timeing.
 * @param reqResponse - response object.
 * @param next - callback function.
 */
var saveResponseToChatQueues = function(reqResponse, next){
    user.findOne({id: reqResponse.author_id},'entity',function(err,data) {
        var queue;
        var options = {multi: true};
        if(data!=undefined){
            if (data.entity == 'Child') {
                queue = {id: "sort_by_children"};
            } else {
                queue = {id: "sort_by_psychologist"};
            }
            //Check if entity exists in desired queue
            chat_queue.findOne(queue,'obj',function(err, data){
                var exist_in_queue = exists(data.obj, reqResponse.author_id);
                if(exist_in_queue) {
                    replaceInQueue(reqResponse,queue,options,next);
                } else {
                    pushToQueue(reqResponse,queue,options,next);
                }
            });
        } else {
            next();
        }
    });
}

/**
 * checkk if id exists in arr ids.
 * @param arr - the array,
 * @param id - the id.
 * @returns {boolean} - if exists.
 */
var exists = function(arr, id){
    for(var i = 0; i < arr.length; i++){
        if(arr[i].user_id == id){
            return true;
        }
    }
    return false;
}

/**
 * replace the response in queue.
 * @param reqResponse - response object.
 * @param queue
 * @param options- default
 * @param next - callback.
 */
var replaceInQueue = function(reqResponse, queue,options, next){
    var update = {$pull: {"obj": {user_id: reqResponse.author_id}}};
    chat_queue.update(queue, update, options, function (err, numAffected) {
        if (err) next(err);
        else {
            pushToQueue(reqResponse,queue,options,next);
        }
    });
}

/**
 * push response to queue.
 * @param reqResponse - response object.
 * @param queue
 * @param options - default
 * @param next - callback function.
 */
var pushToQueue = function(reqResponse,queue,options,next){
    var update = {$push: {"obj": {
    $each: [{user_id: reqResponse.author_id
        , time: reqResponse.time
        , conversation_id: reqResponse.child_id
        , name: reqResponse.author_name}] , $sort: {time:1}}}};
    chat_queue.update(queue, update, options, function (err, numAffected) {
        if (err) next(err);
        else next(null);
    });
}
