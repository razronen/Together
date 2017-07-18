var mongoose = require('mongoose');
var chatConversations = mongoose.model('chat_conversation');
var FCM = require('fcm-node');
var fcm = new FCM(require('../../package.json').server_key);

/**
 * This function responsible of updating the sender status and sending all psychologists and child that attend this
 * converesation push notification.
 * @param reqUpdate - the requested update.
 * @param next - route function.
 */
exports.updateChatConversation = function(reqUpdate, next) {
    var conversation_id = reqUpdate.conversation_id;
    var entity = reqUpdate.entity;
    var id = reqUpdate.id;
    var name = reqUpdate.name;
    var status = reqUpdate.status;
    var deviceId = reqUpdate.deviceId;

    if(entity=='Child'){
        updateChild(id,name,status, function(err,res){
            notifyPsychologists(conversation_id, function(err){
                next(err,res)
            });
        });
    } else {
        updatePsycho(conversation_id,id,name,status, deviceId, function(err,res){
            notifyChild(conversation_id, function(err){
                next(err,res)
            })
        });
    }
}

/**
 * Notify the child that attach to this conversation the psychologist status changed.
 * @param id - of child.
 * @param next - callback function.
 */
var notifyChild = function(id, next){
    chatConversations.find({ 'child_id': id}, function(err,data){
        if(err) next(err)
        else if(data!=undefined && data.length!=0){
            sendUpdateMessage(data[0].child_deviceId, function(){
                next();
            })
        } else {
            next();
        }
    });
}

/**
 * Notifes all the psychologist that attach to this conversation.
 * @param id - of child(conversation)
 * @param next - callback.
 */
var notifyPsychologists = function(id, next){
    chatConversations.findOne({child_id: id},'psycho', function(err,data){
        if(err) next(err);
        else if(data!=undefined){
            var sent = false;
            for(var i = 0; i < data.psycho.length; i++){
                sendUpdateMessage(data.psycho[i].psycho_deviceId, function(){
                    sent = true;
                    next();
                });
            };
            if (!sent) next();
        } else {
            next();
        }
    });
}

/**
 * Sending the update message(information) to the psychologist device by firebase.
 * @param _to- the device ID.
 * @param next - callback function.
 */
var sendUpdateMessage = function (_to,next){
    var message = {
        to: _to,
        collapse_key: 'AIzaSyB_xvM52ngkCFS_gzgnTzaQtJWz4dTe2ws',

        data: {  //you can send only notification or only data(or include both)
            my_key: 'status update'
        }
    }
    fcm.send(message, function(err, response){
        if (err) {
            console.log("Something has gone wrong!");
        } else {
            console.log("Successfully sent with response: ", response);
        }
    });
    next();
}

/**
 * Check if psychologist exists in the conversation, and updates the conversation properties accordingly.
 * @param _child_id - conversation id.
 * @param _id_psycho - specific psychologist id.
 * @param _name - of psycho.
 * @param _status - that changed.
 * @param _deviceId - of psycho.
 * @param next - callback function.
 */
var updatePsycho = function(_child_id, _id_psycho, _name, _status, _deviceId, next){
    var conditions = { child_id: _child_id, 'psycho.psycho_id': _id_psycho }
        , update = { $set: { 'psycho.$.psycho_name': _name,
        'psycho.$.psycho_status': _status,
        'psycho.$.psycho_deviceId': _deviceId} }
        , options = {multi: true};

    //Check if psycho exists, if it does, deletes it.
    chatConversations.findOne(conditions, function(err,data){
        if(err) next();
        else {
            if(data!=undefined){
                //pull psycho from list.
                pullPsychFromList(_child_id, _id_psycho, _name, _status, _deviceId, function(err){
                    if (err) next();
                    else{
                        pushPsychoToList(_child_id, _id_psycho, _name, _status, _deviceId, next);
                    }
                })
            } else {
                pushPsychoToList(_child_id, _id_psycho, _name, _status, _deviceId, next);
            }
        }
    });
}

/**
 * Pulling psychologist from conversation DB object.
 * @param _child_id - child id.
 * @param _id_psycho - psycho id
 * @param _name - name unused
 * @param _status - status unused
 * @param _deviceId - unused
 * @param next
 */
var pullPsychFromList =  function(_child_id, _id_psycho, _name, _status, _deviceId, next) {
    var conditions = { child_id:  _child_id}
        , update = { $pull: { "psycho" : {
        psycho_id: _id_psycho } } }
        , options = { multi: true };
    chatConversations.update(conditions, update, options, function(err, numAffected) {
        if(err) next(err);
        else {
            next();
        }
    });
}

/**
 * Pushing psychologist to conversation DB object.
 * @param _child_id - child id.
 * @param _id_psycho - psycho id
 * @param _name - name unused
 * @param _status - status unused
 * @param _deviceId - unused
 * @param next
 */
var pushPsychoToList = function(_child_id, _id_psycho, _name, _status, _deviceId, next){
        var conditions = { child_id:  _child_id}
            , update = { $push: { "psycho" : {
            psycho_id: _id_psycho,
            psycho_name: _name,
            psycho_status: _status,
            psycho_deviceId: _deviceId} } }
            , options = { multi: true };
        chatConversations.update(conditions, update, options, function(err, numAffected) {
            if(err) next();
            else {
                next();
            }
        });
}

/**
 * Update child according to conversation and status changed properties.
 * @param _id - off child
 * @param _name - of child
 * @param _status - of status
 * @param next - route function.
 */
var updateChild = function(_id, _name, _status, next){
    var conditions = { child_id: _id }
        , update = { child_status: _status }
        , options = { multi: true };

    chatConversations.update(conditions, update, options, function(err,result){
        if(err) {
            console.log(err);
            next();
        }
        else {
            next(err,result);
        }
    });
}

