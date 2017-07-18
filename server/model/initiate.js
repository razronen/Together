var mongoose = require('mongoose');
mongoose.Promise = require('bluebird');
var chat_queue = mongoose.model('chat_queue');
var post_queue = mongoose.model('post_queue');
var userModel = mongoose.model('user');

/**********   INITAIT QUEUES       *********/

var chatQueue1 = { id: "sort_by_children", obj: []};
var chatQueue2 = { id: "sort_by_psychologist", obj: []};
var postQueue = { id: "1", posts: []};

var newChatQueue1 = new chat_queue(chatQueue1);
var newChatQueue2 = new chat_queue(chatQueue2);
var newPostQueue = new post_queue(postQueue);

newChatQueue1.save(function(err,data){
    if(err) console.log(err);
    else console.log("Init sort_by_children chat Queue.");
});

newChatQueue2.save(function(err,data){
    if(err) console.log(err);
    else console.log("Init sort_by_psychologist chat Queue.");
});

newPostQueue.save(function(err,data){
    if(err) console.log(err);
    else console.log("Init post Queue.");
});


/**********   INITAIT MANAGMENT      *********/

var mangUser = {
    id: "managment",
    entity: "Manager",
    name: "Manager",
    icon: "happy_boy2_blue_yellow",
    deviceId: "",
    email: "together.app@gmail.com",
    pass: "door12345",
}

var userInstance = new userModel(mangUser);

userInstance.save(function(err,data){
    if(err) console.log(err);
    else console.log("Init managment user.");
});