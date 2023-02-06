const axios = require("axios");

class AnythinkClient {
  constructor() {
    this.client = axios.create({
      baseURL: "http://localhost:3000",
      timeout: 1000,
    });
  }

  setToken(token) {
    this.client.defaults.headers.common["Authorization"] = `Token ${token}`;
  }

  async healthCheck() {
    return await this.client.get(`/health`);
  }

  async createUser(user) {
    try {
      return await this.client.post(`/api/users`, { user });
    } catch (error) {
      console.error("Create user request failed", error?.response?.data);
      throw error;
    }
  }

  async createItem(item) {
    try {
      const itemRes = await this.client.post(`/api/items`, { item });
      return itemRes.data?.item;
    } catch (error) {
      console.error("Create Item request failed", error?.response?.data);
      throw error;
    }
  }

  async getItem(slug) {
    try {
      const itemRes = await this.client.get(`/api/items/${slug}`);
      return itemRes.data?.item;
    } catch (error) {
      console.error("Create Item request failed", error?.response?.data);
      throw error;
    }
  }
}

module.exports = { AnythinkClient };
