const dispatch = (type) => {
  global.eventEmitter.emit(type);
};

const subscribe = (type, callback) => {
  global.eventEmitter.on(type, callback);
};

const ubsubscribe = (type, callback) => {
  global.eventEmitter.removeListener(type, callback);
};

module.exports = { dispatch, subscribe, ubsubscribe };
