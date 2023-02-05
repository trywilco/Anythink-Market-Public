const axios = require("axios");

const createUser = async () => {
  const username = `user${(Math.random() + 1).toString(36).substring(7)}`;
  const user = {
    username: username,
    email: `${username}@test.work`,
    password: "wilco1234",
  };
  const client = axios.create({
    baseURL: "http://localhost:3000",
    timeout: 10 * 1000,
  });
  try {
    const resCreatedUser = await client.post(`/api/users`, { user });
    console.log({ resCreatedUser: resCreatedUser?.data });
    return resCreatedUser;
  } catch (error) {
    console.error("Create user request failed", error?.response?.data);
    throw error;
  }
};

createUser()
  .then(() => {
    console.log("Finished create user");
    process.exit(0);
  })
  .catch((err) => {
    console.log(
      `Error while granting invites to existing users: ${err.message}`
    );
    process.exit(1);
  });
