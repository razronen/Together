var mongoose = require('mongoose');
var updateModel = mongoose.model('update');

/**
 * Updating update json
 * @param _num of update
 * @param _json - of update
 * @param next - route function.
 */
exports.editUpdate = function(_num, _json, next) {
    var conditions = { _num: _num }
        , update = { json: _json }
        , options = { multi: true };

    updateModel.update(conditions, update, options, function(err, affected, resp){
        if(err)
            next(err);
        else
            next(err,_json);
    });
}