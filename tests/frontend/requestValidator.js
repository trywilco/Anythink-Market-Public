const EventEmitter = require("events");
const { sleep } = require("../utils");
const { expect } = require("@playwright/test");
const { uid } = require("uid");

const eventEmitter = new EventEmitter();

const dispatch = (type) => {
  eventEmitter.emit(type);
};

const subscribe = (type, callback) => {
  eventEmitter.on(type, callback);
};

const unsubscribe = (type, callback) => {
  eventEmitter.removeListener(type, callback);
};

const wrapWithRequestId = (func) => {
  return () => {
    const requestId = uid();
    func(requestId);
    return requestId;
  };
};

const execAndWaitForRequest = async (eventId, func, maxTime = 500) => {
  let eventPromise;
  const eventCallback = () => {
    eventPromise(`The event ${eventId} was sent to Wilco`);
  };

  const subscribePromise = new Promise(async (resolve) => {
    subscribe(eventId, eventCallback);
    eventPromise = resolve;
    await func();
  });

  try {
    const result = await Promise.race([
      new Promise((resolve) => setTimeout(resolve, maxTime)),
      subscribePromise,
    ]);
    expect(result).toBe(`The event ${eventId} was sent to Wilco`);
  } catch {
    throw new Error(`The event ${eventId} was not sent to Wilco`);
  } finally {
    unsubscribe(eventId, eventCallback);
  }
};
//
// const execAndWaitForRequest = async (eventId, func, maxTime = 500) => {
//   let eventReceived = false;
//   const eventCallback = () => {
//     eventReceived = true;
//   };
//
//   subscribe(eventId, eventCallback);
//
//   await func();
//
//   const start = Date.now();
//
//   while (Date.now() - start < maxTime) {
//     if (eventReceived) {
//       console.log(`The event ${eventId} was sent to Wilco`);
//       break;
//     }
//
//     await sleep(100);
//   }
//
//   unsubscribe(eventId, eventCallback);
//
//   try {
//     expect(eventReceived).toBe(true);
//   } catch {
//     throw new Error(`The event ${eventId} was not sent to Wilco`);
//   }
// };

module.exports = {
  dispatch,
  subscribe,
  unsubscribe,
  wrapWithRequestId,
  execAndWaitForRequest,
};
