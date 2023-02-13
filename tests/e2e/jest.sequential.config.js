module.exports = {
  testPathIgnorePatterns: ["/node_modules/", "/concurrent/"],
  globalSetup: "<rootDir>/global-setup.js",
  globalTeardown: "<rootDir>/teardown.js",
};
