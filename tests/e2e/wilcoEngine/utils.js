const { expect } = require("@jest/globals");
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

  try {
    expect(eventReceived).toBe(true);
  } catch {
    throw new Error(`The event ${type} was not sent to Wilco`);
  }
};

module.exports = { execAndWaitForEvent };
