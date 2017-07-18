var mongoose = require('mongoose');
var user = mongoose.model('user');
var chatConversationModel = mongoose.model('chat_conversation');

/**
 * ************     DEPRICATED      *******************
 * Create a user.
 * @param _id
 * @param _name
 * @param _entity
 * @param _phone
 * @param _icon
 * @param _deviceId
 * @param _email
 * @param next
 */
exports.saveUser = function(_id,_name,_entity, _phone, _icon, _deviceId, _email, next) {
    var u = { id: _id
        ,name: _name
        ,entity : _entity
        ,phone : _phone
        ,icon : _icon
        ,deviceId: _deviceId
        ,email: _email}
    var newUser = new user(u);
    newUser.save(function(err, data){
        if(err) next(err, data);
        else {
            if(_entity=='Child'){
                createConversation(_id, _name, "away", _deviceId, function(){
                    next(err,data);
                });
            } else {
                next(err,data);
            }
        }
    });
}

/**
 * create new conversation in db.
 * @param _id - of child
 * @param _name - of child
 * @param _status - online/away/typeing
 * @param _deviceId - for fcm.
 * @param next - callback function.
 */
var createConversation = function(_id,_name,_status, _deviceId, next){
    var chatConversation = new chatConversationModel({
        child_id: _id,
        child_name: _name,
        child_status: _status,
        child_deviceId: _deviceId,
        psycho: []
    })
    var newConversation = new chatConversationModel(chatConversation);
    newConversation.save(function(err,data){
        if(err) console.log("Error adding " + _name + "     " + _id + "     conversation.");
        else {
            console.log("Init " + _name + " chat conversation.");
            next();
        }
    });
}

/**
 * Approved user request registration.
 * @param _id - of user
 * @param _name - of name
 * @param _entity - of approved entity.
 * @param _phone - of cuser
 * @param _icon - of user
 * @param _deviceId - of user
 * @param _email - of user
 * @param next - callback function
 */
exports.approvedUser = function(_id,_name,_entity, _phone, _icon, _deviceId, _email, next) {
    var u = { id: _id
        ,name: _name
        ,entity : _entity
        ,phone : _phone
        ,icon : _icon
        ,deviceId: _deviceId
        ,email: _email}
    var newUser = new user(u);
    newUser.save(function(err, data){
        if(err) next(err, data);
        else {
            if(_entity=='Child'){
                createConversation(_id, _name, "away", _deviceId, function(){
                    next(err,data);
                });
            } else {
                next(err,data);
            }
        }
    });
}