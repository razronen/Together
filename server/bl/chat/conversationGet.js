var mongoose = require('mongoose');
var chatConversations = mongoose.model('chat_conversation');

/**
 * Get the converesation DB object representation.
 * @param _id - child id.
 * @param next - route function.
 */
exports.getChatConversation = function(_id, next) {
    chatConversations.findOne({ child_id: _id},function(err, data){
        if(err) next(err);
        else next(err,data);
    })
}
