var mongoose = require('mongoose');
var response = mongoose.model('response');
var user = mongoose.model('user');
var chat_queue = mongoose.model('chat_queue');

/**
 * Get the responses(chat messages) of the user conversation
 * @param userID - user requested information id.
 * @param userIDforConv - conversation id
 * @param lastMsg - last Msg he has. 99999..-> he doesn't have.
 * @param amount - amount of messages he wants.
 * @param next - route function.
 * @constructor
 */
exports.GetResponses = function(userID,userIDforConv,lastMsg,amount, next){
    var threshold = (lastMsg==-1)?9999999999999:lastMsg;
    user.find({id: userID}, function(err,data1){
        if(err) next(err);
        else{
            if(data1.length==0 || (data1[0].entity=='Child' && userID!=userIDforConv )) next("");
            else {
                //Getting the whole conversation.
                response.find({ $and: [{child_id: userIDforConv}, { num: {$lt: threshold} }]})
                    .sort('num')
                    .exec(function(err,data){
                        if(err) next(err)
                        else next(err, data.slice(Math.max(data.length-amount,0)));
                    });
                }
            }
        });
}

/**
 * Get children representation sorted by each last message timeing.
 * @param next - route function.
 * @constructor
 */
exports.GetChatQueueSortByChildrenLastResponse = function(next){
    chat_queue.findOne({id: 'sort_by_children'},'obj',function(err,data){
        if(err) next(err)
        else next(err,data.obj);
    })

}

/** Get psychologists representation sorted by each last message timeing.
 * @param next - route function.
 * @constructor
 */
exports.GetChatQueueSortByPsychologistLastResponse = function(next){
    chat_queue.findOne({id: 'sort_by_psychologist'},'obj',function(err,data){
        if(err) next(err)
        else next(err,data.obj);
    })
}

