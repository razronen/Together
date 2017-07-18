var mongoose = require('mongoose');
var user = mongoose.model('user');
var chatConversationModel = mongoose.model('chat_conversation');

/**
 * Get all conversation
 * @param next - route function
 * @constructor
 */
exports.GetConversations = function( next) {
    chatConversationModel.find(function(err, data){
        if(err) next(err);
        else next(err,data);
    })
}
