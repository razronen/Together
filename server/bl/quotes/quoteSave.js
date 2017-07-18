var mongoose = require('mongoose');
var userModel = mongoose.model('user');
var quoteModel = mongoose.model('quote');
var helpRequestModel = mongoose.model('help_request');
var FCM = require('fcm-node');
var fcm = new FCM(require('../../package.json').server_key);

/**
 * Saving a quote to quotes model.
 * @param quote - quote object.
 * @param next - route function.
 */
exports.saveQuote = function(quote, next) {
    quoteModel.find().sort('-num').exec(function(err,latestQuote){
        if(err) next(err,latestQuote);
        else {
            //getting last quote num.
            var num = 0;
            if(latestQuote.length!=0){
                num = latestQuote[0].num + 1
            }
            quote.num = num;
            var newQuote = new quoteModel(quote);
            newQuote.save(function(err, data){
                if(err) next(err,data);
                else next(err,data);
            });
        }
    })
}

/**
 * Saving help request to db.
 * saving to use statistics.
 * sending prompt message to all psychologists(and managers).
 * @param helpRequest - object.
 * @param next - route function.
 */
exports.saveHelp = function(helpRequest, next) {
    helpRequestModel.find().sort('-num').exec(function(err,latestRequest){
        if(err) next(err,latestRequest);
        else {
            var num = 0;
            if(latestRequest.length!=0){
                num = latestRequest[0].num + 1
            }
            helpRequest.num = num;
            var newHelpRequest = new helpRequestModel(helpRequest);
            newHelpRequest.save(function(err, data){
                if(err) next(err,data);
                else {
                    saveHelpToUserStatistics(helpRequest, function(err,data1){
                        if(err) next(err,data1);
                        else {
                            userModel.find({ $or: [{entity: 'Manager'},{entity: 'Psychologist'}]}, function(err, data){
                                if(err) next(err,data);
                                else if(data==undefined) next(err,data);
                                else {
                                    for(var i = 0; i < data.length; i++){
                                        SendUpdatedMessage(helpRequest.child_name, data[i].deviceId,
                                            helpRequest.message, num, helpRequest.child_id);
                                    }
                                    next(err,data);
                                }
                            });
                        }
                    });
                }
            });
        }
    })
}

/**
 * Saving to user statistics.
 * @param helpRequest - help object
 * @param next - callback function.
 */
var saveHelpToUserStatistics = function(helpRequest, next){
    var conditions = {id: helpRequest.child_id}
        ,options = {multi: true}
        ,update = {$push: {"immediate_help_messages": {   msg: helpRequest.message } } };
    userModel.update(conditions, update, options, function (err, numAffected) {
        if (err) next(err, numAffected);
        else next(err, numAffected);
    });
}

/**
 * Sending firebase message to psychologist device Id.
 * @param author -of user
 * @param _to -of user
 * @param message -of user
 * @param requestNum -of user
 * @param _child_id -of user
 * @constructor
 */
var SendUpdatedMessage = function(author, _to, message, requestNum,_child_id){
    var message = {
        to: _to,
        collapse_key: 'AIzaSyB_xvM52ngkCFS_gzgnTzaQtJWz4dTe2ws',

        notification: {
            title: "Help Now: " + author,
            body: message
        },

        data: {  //you can send only notification or only data(or include both)
            my_key: author,
            my_another_key: message,
            help_request_num: requestNum,
            child_id: _child_id,
            child_name: author
        }
    }
    fcm.send(message, function(err, response){
        if (err) {
            console.log("Something has gone wrong!");
        } else {
            console.log("Successfully sent with response: ", response);
        }
    });
}