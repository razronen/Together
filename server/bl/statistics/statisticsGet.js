var mongoose = require('mongoose');
var userModel = mongoose.model('user');
var statisticsModel = mongoose.model('time_statistics');

/**
 * Get all the children that have name as substring in their name.
 * @param name - the subtext name
 * @param next -route function
 */
exports.getChildrenRepresentationByName = function(name, next) {
    userModel.find({"name": {$regex: ".*" + name + ".*"}, entity : "Child"}, function(err,data){
        if(err) next(err,data);
        else next(err,data);
    });
}

/**
 * Get all the psychologists that have name as substring in their name.
 * @param name - the subtext name
 * @param next -route function
 */
exports.getPsychologistRepresentationByName = function(name, next) {
    userModel.find({"name": {$regex: ".*" + name + ".*"}, $or : [{entity : "Psychologist"}, {entity: "Manager"}]}, function(err,data){
        if(err) next(err,data);
        else next(err,data);
    });
}

/**
 * Building the time statistics-day statistics object, object by object.
 * Self explentory.
 * @param from - start date.
 * @param to - end date
 * @param next - route function.
 */
exports.timeStatistics = function(from, to, next) {
    var public_post = 0;
    var private_post = 0;
    var response = 0;
    var chat_started = 0;
    var chat_responses = 0;
    var inserted_to_quotes = 0;
    var user_sign_up = 0;
    var user_delete = 0;
    var psychologist_action = "";
    var child_action = "";
    statisticsModel.find({ public_post : {$gt: from, $lt: to}},function(err,data){
        if(err) next(err,data);
        else {
            if(data!=undefined && data.length>0){
                public_post = data.length;
            }
            statisticsModel.find({ private_post : {$gt: from, $lt: to}},function(err,data){
                if(err) next(err,data);
                else {
                    if(data!=undefined && data.length>0){
                        private_post = data.length;
                    }
                    statisticsModel.find({ response : {$gt: from, $lt: to}},function(err,data){
                        if(err) next(err,data);
                        else {
                            if(data!=undefined && data.length>0){
                                response = data.length;
                            }
                            statisticsModel.find({ chat_started : {$gt: from, $lt: to}},function(err,data){
                                if(err) next(err,data);
                                else {
                                    if(data!=undefined && data.length>0){
                                        chat_started = data.length;
                                    }
                                    statisticsModel.find({ chat_responses : {$gt: from, $lt: to}},function(err,data){
                                        if(err) next(err,data);
                                        else {
                                            if(data!=undefined && data.length>0){
                                                chat_responses = data.length;
                                            }
                                            statisticsModel.find({ inserted_to_quotes : {$gt: from, $lt: to}},function(err,data){
                                                if(err) next(err,data);
                                                else {
                                                    if(data!=undefined && data.length>0){
                                                        inserted_to_quotes = data.length;
                                                    }
                                                    statisticsModel.find({ user_sign_up : {$gt: from, $lt: to}},function(err,data){
                                                        if(err) next(err,data);
                                                        else {
                                                            if(data!=undefined && data.length>0){
                                                                user_sign_up = data.length;
                                                            }
                                                            statisticsModel.find({ user_delete : {$gt: from, $lt: to}},function(err,data){
                                                                if(err) next(err,data);
                                                                else {
                                                                    if(data!=undefined && data.length>0){
                                                                        user_delete = data.length;
                                                                    }
                                                                    var output = {
                                                                       _public_post: public_post,
                                                                       _private_post: private_post,
                                                                       _response: response,
                                                                       _chat_started: chat_started,
                                                                       _chat_responses: chat_responses,
                                                                       _inserted_to_quote: inserted_to_quotes,
                                                                       _user_sign_up: user_sign_up,
                                                                       _user_delete: user_delete
                                                                    }
                                                                    next(err,output);
                                                                    //statisticsModel.find({ "psychologist_action.time" : {$gt: 0}, "psychologist_action.name": {$regex: ".*d.*"}},function(err,data){
                                                                    //    if(err) next(err,data);
                                                                    //    else {
                                                                    //        console.log("reached9:" + data);
                                                                    //        if(data!=undefined && data.length>0){
                                                                    //            var psychos = [];
                                                                    //            for(var i = 0; i > data.length; i++){
                                                                    //                psychos.push(data[i].psychologist_action.name);
                                                                    //            }
                                                                    //            console.log("reached10:" + data);
                                                                    //            console.log(psychos);
                                                                    //        }
                                                                    //        console.log("reached11:" + data);
                                                                    //        next(err,data);
                                                                    //    }
                                                                    //});
                                                                }
                                                            });
                                                        }
                                                    });
                                                }
                                            });
                                        }
                                    });
                                }
                            });
                        }
                    });
                }
            });
        }
    });
}

