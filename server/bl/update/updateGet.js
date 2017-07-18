var mongoose = require('mongoose');
var updateModel = mongoose.model('update');

/**
 * Get amount updates from last msf.
 * @param lastMsg - that the user have.
 * @param amount -how many updates.
 * @param next - route function.
 */
exports.getUpdate = function(lastMsg, amount, next){
    var threshold = (lastMsg==-1)?9999999999999:lastMsg;
    updateModel.find({ num: {$lt: threshold} })
        .sort('num')
        .exec(function(err,data){
            if(err) next(err)
            else next(err, data.slice(Math.max(data.length-amount,0)));
        });
}
