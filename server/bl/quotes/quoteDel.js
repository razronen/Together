var mongoose = require('mongoose');
var quoteModel = mongoose.model('quote');

/**
 * Deleting a queue.
 * @param num -of queue from all queues
 * @param next - callback function.
 */
exports.delQuote = function(num, next) {
    quoteModel.find().remove({'num': num}).remove(function (err, data) {
        if (err) next(err);
        else {
            adjustNextQuote(parseInt(num)+1, function(err){
                next(err,num);
            });
        }
    });
}

/**
 * adjusting all other quotes numbers.
 * @param _num - number that got deleted/upgrade
 * @param next - self or calllback function.
 */
var adjustNextQuote = function(_num, next){
    var conditions = { num: _num }
        , update = { num: parseInt(_num) -1 }
        , options = { multi: true };

    quoteModel.update(conditions, update, options, function(err, affected, resp){
        if(err)  next(err);
        else if (affected.nModified == 0) next(resp);
        else adjustNextQuote(parseInt(_num) + 1, function(err){
            next(err);
        });
    });
}
