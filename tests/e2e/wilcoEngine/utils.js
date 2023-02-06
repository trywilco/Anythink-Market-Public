const { sleep } = require("../../utils");
const { subscribe, ubsubscribe } = require("./wilcoEngineEvents");

const execAndWaitForEvent = async (type, func, maxTime = 500) => {
  let eventReceived = false;
  const eventCallback = () => {
    eventReceived = true;
  };

  subscribe(type, eventCallback);

  await func();

  const start = Date.now();

  while (Date.now() - start < maxTime) {
    if (eventReceived) {
      break;
    }

    await sleep(100);
  }

  ubsubscribe(type, eventCallback);

  if (!eventReceived) {
    throw new Error(`Event ${type} not caught within ${maxTime}ms`);
  }
};

module.exports = { execAndWaitForEvent };
