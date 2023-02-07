function randomString(length = 10) {
  const result = [];
  const characters = "abcdefghijklmnopqrstuvwxyz0123456789";
  const charactersLength = characters.length;
  for (let i = 0; i < length; i++) {
    result.push(
      characters.charAt(Math.floor(Math.random() * charactersLength))
    );
  }
  return result.join("");
}

function randomUserInfo(info = null) {
  const username = `user${randomString()}`;
  return {
    username: username,
    email: `${username}@test.work`,
    password: randomString(),
    ...info,
  };
}

module.exports = {
  randomString,
  randomUserInfo,
};
