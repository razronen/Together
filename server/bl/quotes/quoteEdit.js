var mongoose = require('mongoose');
var quoteModel = mongoose.model('quote');

/**
 * Editing author or content of quote.
 * @param quote - quote object.
 * @param next - callback function.
 */
exports.editQuote = function(quote, next) {
    var conditions = { num: quote.num }
        , update = { content: quote.content, creator: quote.creator }
        , options = { multi: true };

    quoteModel.update(conditions, update, options, function(err, affected, resp){
        if(err)  next(err);
        else next(err,quote);
    });
}
