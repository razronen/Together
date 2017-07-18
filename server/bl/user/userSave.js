var mongoose = require('mongoose');
var user = mongoose.model('user');
var FCM = require('fcm-node');
var fcm = new FCM(require('../../package.json').server_key);
var time_statistics = mongoose.model('time_statistics');
var chatConversationModel = mongoose.model('chat_conversation');
var rand = require("generate-key");

/**
 * Save user to db
 * save to time statistics
 * init chat conversation.(if child)
 * @param newUser - user object.
 * @param next - route function
 */
exports.saveUser = function(newUser, next) {
    newUser.id = rand.generateKey().toString();
    saveNewUserToTimeStatistics();
    var newUserInstance = new user(newUser);
    newUserInstance.save(function(err,data){
        if(err) next(err,data);
        else {
            if(newUser.entity=='Child'){
                createConversation(newUser.id, newUser.name, "away", newUser.deviceId, function(){
                    next(err,data);
                });
            } else {
                next(err,data);
            }
        }
    })
}

/**
 * Creates a chat conversation db object.
 * @param _id - of user
 * @param _name- of user
 * @param _status- of user
 * @param _deviceId- of user
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
 * Save a new user profile registed to time statistics.
 */
var saveNewUserToTimeStatistics = function(){
    var newUser = {user_sign_up: (new Date).getTime()};
    var NewUser = time_statistics(newUser);
    NewUser.save();
};
