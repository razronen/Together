var postsSaveBL = require('../bl/dev/postsSave');
var commentsSaveBL = require('../bl/dev/addComment');
var userSaveBL = require('../bl/dev/userSave');
var conversationEditBL = require('../bl/dev/conversationEdit');
var responseSaveBL = require('../bl/dev/responseSave');
var responseSaveBLReal = require('../bl/chat/responseSave');
var conversationEditBLReal = require('../bl/chat/conversationEdit');
var quoteSaveBL = require('../bl/quotes/quoteSave');
var quoteEditBL = require('../bl/quotes/quoteEdit');
var loginSaveBL = require('../bl/login/loginSave');

/**
 * Saving post
 * @param req - request
 * @param res - response
 * @param next
 */
exports.savePost = function(req,res,next) {
    postsSaveBL.savePost(req.params.is_public,
                            req.params.title,
                            req.params.message,
                            req.params.publisher,
                            req.params.publisherID,
                            req.params.date,
                            req.params.id,function(err,data){
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
 * Adding commet
 * @param req - request
 * @param res - response
 * @param next
 */
exports.addComment = function(req,res,next) {
    commentsSaveBL.saveComment(req.params.postID,
        req.params.message,
        req.params.publisher,
        req.params.publisherID,
        req.params.date,
        req.params.id,function(err,data){
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
 * Saving user.
 * @param req - request
 * @param res - response
 * @param next
 */
exports.saveUser = function(req,res,next) {
    userSaveBL.saveUser(req.params.id,
        req.params.name,
        req.params.entity,
        req.params.phone,
        req.params.icon,
        req.params.deviceId,
        req.params.email, function(err,data){
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
 * Saving response
 * @param req - request
 * @param res - response
 * @param next
 */
exports.saveResponse = function(req,res,next) {
    responseSaveBL.saveResponse(req.params.child_id,
        req.params.num,
        req.params.time,
        req.params.message,
        req.params.author_id,
        req.params.author_name,
        req.params.author_entity,function(err,data){
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
 * Sending notification
 * @param req - request
 * @param res - response
 * @param next
 * @constructor
 */
exports.SendNotification = function(req,res,next) {
    responseSaveBL.SendNotification(req.params.child_id,
        req.params.num,
        req.params.time,
        req.params.message,
        req.params.author_id,
        req.params.author_name,
        req.params.author_entity,function(err,data){
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
 * Saving response
 * @param req - request
 * @param res - response.
 * @param next
 * @constructor
 */
exports.SendResponse = function(req,res,next) {
    var reqResponse = {
        child_id: req.params.child_id,
        num: req.params.chat_num,
        time: req.params.time,
        message: req.params.message,
        image: req.params.image,
        image_x: req.params.image_x,
        image_y: req.params.image_y,
        link: req.params.link,
        author_id: req.params.author_id,
        author_name: req.params.author_name,
        author_entity: req.params.author_entity
    }
    responseSaveBLReal.saveResponse(reqResponse,function(err,data){
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
 * Updaing chat conversation
 * @param req - request
 * @param res - response
 * @param next
 */
exports.updateChatConversation = function(req,res,next) {
    var reqResponse = {
        conversation_id: req.params.conversation_id,
        entity: req.params.entity,
        id: req.params.id,
        name: req.params.name,
        status: req.params.status,
        deviceId: req.params.deviceId
    }
    conversationEditBLReal.updateChatConversation(reqResponse,function(err,data){
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
 * Gettings all conversations
 * @param req - request
 * @param res - response
 * @param next
 */
exports.GetConversations = function(req,res,next) {
    conversationEditBL.GetConversations(function(err,data){
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
 * Saving quote
 * @param req - request
 * @param res - response
 * @param next
 */
exports.saveQuote = function(req,res,next) {
    var body = {
        content: req.params.content,
        creator: req.params.creator,
    }
    quoteSaveBL.saveQuote(body, function(err,data){
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
    var body = {
        num: req.params.num,
        content: req.params.content,
        creator: req.params.creator,
    }
    quoteEditBL.editQuote(body, function(err,data){
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
 * Approve user.
 * @param req - request
 * @param res - response
 * @param next
 */
exports.approvedUser = function(req,res,next) {
    var body = {
        code: req.params.code,
        first_name: req.params.first_name,
        last_name: req.params.last_name,
        entity: req.params.entity,
    }
    loginSaveBL.approvedUser(body, function(err,data){
        if(err){
            console.log(err);
            next(err);
        } else {
            console.log("get:" + data);
            res.send(data);
        }
    })
}