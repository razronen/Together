var mongoose = require('mongoose');
var shiftModel = mongoose.model('pending_shift');
var updateModel = mongoose.model('update');
var rand = require("generate-key");

/**
 * Save shift to DB.
 * @param shift - shift object.
 * @param next - route function.
 */
exports.saveShift = function(shift, next) {
    shift.id = rand.generateKey().toString();
    var newShift = new shiftModel(shift);
    newShift.save(function(err, data){
        if(err) next(err, data);
        else {
            saveToUpdates(shift);
            next(err, data);
        }
    });
}

/**
 * Saving shift request to updates feeds.
 * @param shift - shift object.
 */
var saveToUpdates = function(shift){
    var shiftUpdate = {
        num: 1,
        time: (new Date).getTime(),
        type: 'SHIFT_UPDATE',
        json: '{ "start": "'+ shift.start + '" , "end" : "' + shift.end + '", "name": "'
               + shift.psycho_name + '" , "id" : "' + shift.id +'"  }'
    }
    updateModel.find()
        .sort('num')
        .exec(function(err,data1){
            if(err) next(err)
            else {
                if (data1.length!=0) {
                    shiftUpdate.num = data1[data1.length -1].num+1;
                }
                var updateInstance = new updateModel(shiftUpdate);
                updateInstance.save();
            }}
        );
}
