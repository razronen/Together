var mongoose = require('mongoose');
var quoteModel = mongoose.model('quote');
var helpRequestModel = mongoose.model('help_request');
var time_statistics = mongoose.model('time_statistics');
var userModel = mongoose.model('user');

/**
 * Get all quotes by num order.
 * @param next - route function.
 */
exports.getAllQuotes = function(next) {
    quoteModel.find().sort('-num').exec(function(err,data){
        if(err) next(err,latestQuote);
        else  next(err,data);
    })

}

/**
 * Get specific quote by number.
 * @param _num - number of quote.
 * @param next - route function.
 */
exports.getQuote = function(_num, next) {
    quoteModel.findOne({num: _num}, function(err,data){
        if(err) next(err,data);
        else next(err,data);
    })
}

/**
 * Get specific help request.
 * @param _num - num of help request.
 * @param next
 */
exports.getHelp = function(_num, next) {
    helpRequestModel.findOne({num: _num}, function(err,data){
        if(err) next(err,data);
        else if(data==undefined) next(err,data);
        else {
            next(err,data.child_id);
        }
    })
}

/**
 * Saving quote to time/Day statistics.
 * @param next - route function.
 */
exports.getReadingQuotes = function(next) {
    var readingQuotes = {inserted_to_quotes: (new Date).getTime()};
    var ReadingQuotes = time_statistics(readingQuotes);
    ReadingQuotes.save(function(err,data){
        next(err,data);
    });

}

/**
 * Updating user statistics by help request answered or not  - for psychologists.
 * @param _id - psychologist.
 * @param helped - true/false.
 * @param next - route function.
 */
exports.getAnsweredHelp = function(_id,helped,next) {
    var conditions = {id: _id}
        ,options = {multi: true};
    var update = undefined;
    if(helped=='true'){
        update = { $inc : {immediate_help_messages_answered: 1 }};
    } else {
        update = { $inc : {immediate_help_messages_not_answered: 1 }};
    };
    userModel.update(conditions, update, options, function (err, numAffected) {
        if (err) next(err, numAffected);
        else next(err, numAffected);
    });

}