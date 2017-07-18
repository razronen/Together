var postsSaveBL = require('../bl/posts/postsSave');
var commentsSaveBL = require('../bl/comments/commentsSave');
var responseSaveBL = require('../bl/chat/responseSave');
var userSaveBL = require('../bl/user/userSave');
var calendarSaveBL = require('../bl/calendar/shiftSave');
var quoteSaveBL = require('../bl/quotes/quoteSave');
var loginSaveBL = require('../bl/login/loginSave');

/**
 * Save post
 * @param req - request
 * @param res - response
 * @param next
 */
exports.savePosts = function(req,res,next) {
    postsSaveBL.savePosts(req.body, function(err,data){
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
 * Save comment
 * @param req - request
 * @param res - response
 * @param next
 */
exports.saveComments = function(req,res,next) {
    commentsSaveBL.saveComments(req.body, function(err,data){
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
 * Edit comment
 * @param req - request
 * @param res - response
 * @param next
 */
exports.editComment = function(req,res,next) {
    postsSaveBL.editComments(req.body, function(err,data){
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
 * Save response
 * @param req - request
 * @param res - response
 * @param next
 */
exports.saveResponse = function(req,res,next) {
    responseSaveBL.saveResponse(req.body, function(err,data){
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
 * Save user
 * @param req - request
 * @param res - response
 * @param next
 */
exports.saveUser = function(req,res,next) {
    userSaveBL.saveUser(req.body, function(err,data){
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
 * Save shift
 * @param req - request
 * @param res - response
 * @param next
 */
exports.saveShift = function(req,res,next) {
    calendarSaveBL.saveShift(req.body, function(err,data){
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
 * Save quote
 * @param req - request
 * @param res - response
 * @param next
 */
exports.saveQuote = function(req,res,next) {
    quoteSaveBL.saveQuote(req.body, function(err,data){
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
 * Save help request
 * @param req - request
 * @param res - response
 * @param next
 */
exports.saveHelp = function(req,res,next) {
    quoteSaveBL.saveHelp(req.body, function(err,data){
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
 * Save login request
 * @param req - request
 * @param res - response
 * @param next
 */
exports.saveLoginRequest = function(req,res,next) {
    loginSaveBL.saveLoginRequest(req.body, function(err,data){
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
 * Save approval of user
 * @param req - request
 * @param res - response
 * @param next
 */
exports.approvedUser = function(req,res,next) {
    loginSaveBL.approvedUser(req.body, function(err,data){
        if(err){
            console.log(err);
            next(err);
        } else {
            console.log("get:" + data);
            res.send(data);
        }
    })
}