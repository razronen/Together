var commentsEditBL = require('../bl/comments/commentsEdit');
var postsEditBL = require('../bl/posts/postsEdit');
var chatEditBL = require('../bl/chat/conversationEdit');
var quoteEditBL = require('../bl/quotes/quoteEdit');
var updateEditBL = require('../bl/update/updateEdit');

/**
 * Edit a comment
 * @param req - request
 * @param res - response
 * @param next
 */
exports.editComment = function(req,res,next) {
    commentsEditBL.editComment(req.params.id, req.body, function(err,data){
        if(err){
            console.log(err);
            next(err);
        } else {
            console.log("get:" + data);
            res.send(data);
        }
    })
}

/**
 * Editing a post
 * @param req - request
 * @param res - response
 * @param next
 */
exports.editPost = function(req,res,next) {
    postsEditBL.editPost(req.params.id,req.body, function(err,data){
        if(err){
            console.log(err);
            next(err);
        } else {
            console.log("get:" + data);
            res.send(data);
        }
    })
}

/**
 * Updating a chat conversation
 * @param req - request
 * @param res - response
 * @param next
 */
exports.updateChatConversation = function(req,res,next) {
    var reqResponse = {
        conversation_id: req.body.conversation_id,
        entity: req.body.entity,
        id: req.body.id,
        name: req.body.name,
        status: req.body.status,
        deviceId: req.body.deviceId
    }
    chatEditBL.updateChatConversation(reqResponse,function(err,data){
        if(err){
            console.log(err);
            next(err);
        } else {
            console.log("get:" + data);
            res.send(data);
        }
    })
}

/**
 * Editing a quote
 * @param req - request
 * @param res - response
 * @param next
 */
exports.editQuote = function(req,res,next) {
    quoteEditBL.editQuote(req.body, function(err,data){
        if(err){
            console.log(err);
            next(err);
        } else {
            console.log("get:" + data);
            res.send(data);
        }
    })
}

/**
 * Editing an update
 * @param req - request
 * @param res - response.
 * @param next
 */
exports.editUpdate = function(req,res,next) {
    updateEditBL.editUpdate(req.params.num,req,params.json, function(err,data){
        if(err){
            console.log(err);
            next(err);
        } else {
            console.log("get:" + data);
            res.send(data);
        }
    })
}
