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

  async ping() {
    return await this.client.get(`/api/ping`);
  }

  async createUser(user) {
    const result = await this.client.post(`/api/users`, { user });
    return result.data?.user;
  }

  async loginUser(email, password) {
    const result = await this.client.post(`/api/users/login`, {
      user: { email, password },
    });
    return result.data?.user;
  }

  async getUser() {
    const result = await this.client.get(`/api/user`);
    return result.data?.user;
  }

  async updateUser(userInfo) {
    const result = await this.client.put(`/api/user`, { user: userInfo });
    return result.data?.user;
  }

  async createItem(item) {
    const itemRes = await this.client.post(`/api/items`, { item });
    return itemRes.data?.item;
  }

  async deleteItem(slug) {
    await this.client.delete(`/api/items/${slug}`);
  };
  
  async updateItem(slug, item) {
    const itemRes = await this.client.put(`/api/items/${slug}`, { item });
    return itemRes.data?.item;
  }

  async getItem(slug) {
    const itemRes = await this.client.get(`/api/items/${slug}`);
    return itemRes.data?.item;
  }

  async followUser(username) {
    const profileRes = await this.client.post(
      `/api/profiles/${username}/follow`
    );
    return profileRes.data?.profile;
  }

  async unfollowUser(username) {
    const profileRes = await this.client.delete(
      `/api/profiles/${username}/follow`
    );
    return profileRes.data?.profile;
  }

  async getProfile(username) {
    const profileRes = await this.client.get(`/api/profiles/${username}`);
    return profileRes.data?.profile;
  }
}

module.exports = { AnythinkClient };
