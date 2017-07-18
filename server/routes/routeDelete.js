var commentsDeleteBL = require('../bl/comments/commentsDelete');
var postsDeleteBL = require('../bl/posts/postsDelete');
var quoteDelBL = require('../bl/quotes/quoteDel');

/**
 * Delete comment
 * @param req - request
 * @param res - response
 * @param next
 */
exports.deleteComment = function(req,res,next) {
    commentsDeleteBL.deleteComment(req.params.commentID,req.params.postID, function(err,data){
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
 * Ddelete post
 * @param req - request
 * @param res - response
 * @param next
 */
exports.deletePost = function(req,res,next) {
    postsDeleteBL.deletePost(req.params.id, function(err,data){
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
 * Delete quote.
 * @param req - request
 * @param res - response
 * @param next
 */
exports.delQuote = function(req,res,next) {
    quoteDelBL.delQuote(req.params.num, function(err,data){
        if(err){
            console.log(err);
            next(err);
        } else {
            console.log("get:" + data);
            res.send(data);
        }
    })
}