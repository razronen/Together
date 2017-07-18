var postsGetBL = require('../bl/posts/postsGet');
var commentsGetBL = require('../bl/comments/commentsGet');
var responseGetBL = require('../bl/chat/responseGet');
var conversationGetBL = require('../bl/chat/conversationGet');
var userGetBL = require('../bl/user/userGet');
var calendarGetBL = require('../bl/calendar/shiftGet');
var quoteGetBL = require('../bl/quotes/quoteGet');
var statisticsGetBL = require('../bl/statistics/statisticsGet');
var updateGetBL = require('../bl/update/updateGet');
var loginGetBL = require('../bl/login/loginGet');

/**
 * Get posts
 * @param req - request
 * @param res - response
 * @param next
 */
exports.getPosts = function(req,res,next) {
    postsGetBL.getPosts(req.params.userID,req.params.lastTime,req.params.howMany, function(err,data){
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
 * Get a specific post
 * @param req -request
 * @param res - response
 * @param next
 */
exports.getSpecificPost = function(req,res,next) {
    postsGetBL.getSpecificPost(req.params.postID, function(err,data){
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
 * Search Posts.
 * @param req - request
 * @param res - response
 * @param next
 */
exports.searchPosts = function(req,res,next) {
    postsGetBL.searchPosts(req.body.userID,req.body.string,req.body.howMany, function(err,data){
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
 * Get Comment
 * @param req - request
 * @param res - response
 * @param next
 */
exports.getComments = function(req,res,next) {
    commentsGetBL.getComments(req.params.postID,req.params.amount, function(err,data){
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
 * Get all posts.
 * @param req - request
 * @param res - response
 * @param next
 */
exports.getAllPosts = function(req,res,next) {
    postsGetBL.getAllPosts(function(err,data){
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
 * Get all comments.
 * @param req - request
 * @param res - response
 * @param next
 */
exports.getAllComments = function(req,res,next) {
    commentsGetBL.getAllComments(function(err,data){
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
 * get Quote of unread post.
 * @param req
 * @param res
 * @param next
 */
exports.getPostsQueue = function(req,res,next) {
    postsGetBL.getPostsQueue(function(err,data){
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
 * Get Responses
 * @param req - request
 * @param res - response
 * @param next
 * @constructor
 */
exports.GetResponses = function(req,res,next) {
    responseGetBL.GetResponses(req.params.userID,
        req.params.userIDforConv,
        req.params.lastMsg,
        req.params.howMany,
        function(err,data){
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
 * Get response queue sorted by last response of child.
 * @param req - request
 * @param res - response
 * @param next
 * @constructor
 */
exports.GetChatQueueSortByChildrenLastResponse = function(req,res,next) {
    responseGetBL.GetChatQueueSortByChildrenLastResponse(function(err,data){
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
 * Get resoponse queue sorted by last message by psycho.
 * @param req - request
 * @param res - response
 * @param next
 * @constructor
 */
exports.GetChatQueueSortByPsychologistLastResponse = function(req,res,next) {
    responseGetBL.GetChatQueueSortByPsychologistLastResponse(function(err,data){
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
 * Get children by name.
 * @param req - request
 * @param res - response
 * @param next
 * @constructor
 */
exports.GetChildrenByName = function(req,res,next) {
    userGetBL.GetChildrenByName(req.body.name,function(err,data){
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
 * Get chat conversation
 * @param req - request
 * @param res - response
 * @param next
 */
exports.getChatConversation = function(req,res,next) {
    conversationGetBL.getChatConversation(req.params.id,function(err,data){
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
 * Approve shift
 * @param req - request
 * @param res - response
 * @param next
 */
exports.approveShift = function(req,res,next) {
    calendarGetBL.approveShift(req.params.id, req.params.num, req.params.approve, function(err,data){
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
 * get Shift Calendar
 * @param req - request
 * @param res - response
 * @param next
 */
exports.shiftCalendar = function(req,res,next) {
    calendarGetBL.shiftCalendar(req.params.max, function(err,data){
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
 * Gete a specific shift.
 * @param req - request
 * @param res - response
 * @param next
 */
exports.getShift = function(req,res,next) {
    calendarGetBL.getShift(req.params.id, function(err,data){
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
 * Get the approved shifts by psychos.
 * @param req - request
 * @param res - response
 * @param next
 */
exports.getApprovedShiftByPsychoId = function(req,res,next) {
    calendarGetBL.getApprovedShiftByPsychoId(req.params.id, function(err,data){
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
 * Get all the quotes.
 * @param req - request
 * @param res - response
 * @param next
 */
exports.getAllQuotes = function(req,res,next) {
    quoteGetBL.getAllQuotes(function(err,data){
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
 * Get Quote
 * @param req - request
 * @param res - response
 * @param next
 */
exports.getQuote = function(req,res,next) {
    quoteGetBL.getQuote(req.params.num, function(err,data){
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
 * Get Help request
 * @param req - request
 * @param res - response
 * @param next
 */
exports.getHelp = function(req,res,next) {
    quoteGetBL.getHelp(req.params.num, function(err,data){
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
 * Get the children represntation by name
 * @param req - request
 * @param res - response
 * @param next
 */
exports.getChildrenRepresentationByName = function(req,res,next) {
    statisticsGetBL.getChildrenRepresentationByName(req.body.name, function(err,data){
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
 * Get the psychologist repsresnatation by name
 * @param req - request
 * @param res - response
 * @param next
 */
exports.getPsychologistRepresentationByName = function(req,res,next) {
    statisticsGetBL.getPsychologistRepresentationByName(req.body.name, function(err,data){
        if(err){
            console.log(err);
            next(err);
        } else {
            console.log("get:" + data);
            res.send(data);
        }
    })
};

/**
 * Get login time
 * @param req - request
 * @param res - response
 * @param next
 */
exports.getLoginTime = function(req,res,next) {
    userGetBL.getLoginTime(req.params.id,req.params.time, function(err,data){
        if(err){
            console.log(err);
            next(err);
        } else {
            console.log("get:" + data);
            res.send(data);
        }
    })
};

/**
 * Get user details
 * @param req - request
 * @param res - response
 * @param next
 */
exports.getUserDetails = function(req,res,next) {
    userGetBL.getUserDetails(req.params.id, function(err,data){
        if(err){
            console.log(err);
            next(err);
        } else {
            console.log("get:" + data);
            res.send(data);
        }
    })
};

/**
 * Get reading quotes
 * @param req - request
 * @param res - response
 * @param next
 */
exports.getReadingQuotes = function(req,res,next) {
    quoteGetBL.getReadingQuotes(function(err,data){
        if(err){
            console.log(err);
            next(err);
        } else {
            console.log("get:" + data);
            res.send(data);
        }
    })
};

/**
 * Get destroy application num.
 * @param req - request
 * @param res - response
 * @param next
 */
exports.getDestroyApplication = function(req,res,next) {
    userGetBL.getDestroyApplication(function(err,data){
        if(err){
            console.log(err);
            next(err);
        } else {
            console.log("get:" + data);
            res.send(data);
        }
    })
};

/**
 * Get time/Day statistics
 * @param req - request
 * @param res - response
 * @param next
 */
exports.timeStatistics = function(req,res,next) {
    statisticsGetBL.timeStatistics(req.params.from ,req.params.to,function(err,data){
        if(err){
            console.log(err);
            next(err);
        } else {
            console.log("get:" + data);
            res.send(data);
        }
    })
};

/**
 * Get answered help request
 * @param req - request
 * @param res - response
 * @param next
 */
exports.getAnsweredHelp = function(req,res,next) {
    quoteGetBL.getAnsweredHelp(req.params.id ,req.params.helped,function(err,data){
        if(err){
            console.log(err);
            next(err);
        } else {
            console.log("get:" + data);
            res.send(data);
        }
    })
};

/**
 * Get updates
 * @param req - request
 * @param res - response
 * @param next
 */
exports.getUpdate = function(req,res,next) {
    updateGetBL.getUpdate(req.params.num,req.params.amount ,function(err,data){
        if(err){
            console.log(err);
            next(err);
        } else {
            console.log("get:" + data);
            res.send(data);
        }
    })
};

/**
 * Check if email exists
 * @param req - request
 * @param res - response
 * @param next
 */
exports.emailExists = function(req,res,next) {
    loginGetBL.emailExists(req.params.email ,function(err,data){
        if(err){
            console.log(err);
            next(err);
        } else {
            console.log("get:" + data);
            res.send(data);
        }
    })
};

/**
 * Check if this device Id is approved
 * @param req - request
 * @param res - response
 * @param next
 */
exports.isApproved = function(req,res,next) {
    loginGetBL.isApproved(req.params.code ,function(err,data){
        if(err){
            console.log(err);
            next(err);
        } else {
            console.log("get:" + data);
            res.send(data);
        }
    })
};

/**
 * Authentication.
 * @param req - request
 * @param res - response.
 * @param next
 */
exports.checkLogin = function(req,res,next) {
    loginGetBL.checkLogin(req.body.email, req.body.pass ,function(err,data){
        if(err){
            console.log(err);
            next(err);
        } else {
            console.log("get:" + data);
            res.send(data);
        }
    })
};

/**
 * Send a password to email - 'forgot your password'
 * @param req - request
 * @param res- response
 * @param next
 */
exports.sendPasswordToMail = function(req,res,next) {
    loginGetBL.sendPasswordToMail(req.body.email, req.body.code ,function(err,data){
        if(err){
            console.log(err);
            next(err);
        } else {
            console.log("get:" + data);
            res.send(data);
        }
    })
};