require("dotenv").config();
const mongoose = require("mongoose");
require("../models/User");

const User = mongoose.model("User");

const connectedToDatabase = () => {
  const connection = process.env.MONGODB_URI || "mongodb://localhost:27017";
  mongoose.connect(connection);
  mongoose.set("debug", true);
};

async function main() {
  connectedToDatabase();

  const regularUser = new User();
  regularUser.username = `regularUser`;
  regularUser.email = `regularuser@gmail.com`;
  regularUser.role = `user`;
  regularUser.setPassword(`123456`);
  await regularUser.save();

  const adminUser = new User();
  adminUser.username = `adminUser`;
  adminUser.email = `adminuser@gmail.com`;
  adminUser.role = `admin`;
  adminUser.setPassword(`123456`);
  await adminUser.save();
}

main()
  .then(() => {
    console.log("Finished DB seeding");
    process.exit(0);
  })
  .catch((err) => {
    console.log(`Error while running DB seed: ${err.message}`);
    process.exit(1);
  });
