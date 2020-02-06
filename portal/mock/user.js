
const tokens = {
  admin: 'admin-token',
  editor: 'editor-token',
  user11: 'user-token'
}

const users = {
  'admin-token': {
    roles: ['admin'],
    permissions: ['user:add', 'user:delete', 'user:update', 'user:list'],
    avatar: 'https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif',
    nickname: '超级管理员'
  },
  'editor-token': {
    roles: ['editor'],
    permissions: ['user:update', 'user:list'],
    avatar: 'https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif',
    nickname: '普通编辑'
  },
  'user-token': {
    roles: ['user'],
    permissions: ['user:list'],
    avatar: 'https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif',
    nickname: '普通用户'
  }
}

export default [
  // user login
  {
    url: '/login',
    type: 'post',
    response: config => {
      const { account } = config.body
      const token = tokens[account]

      // mock error
      if (!token) {
        return {
          code: 60204,
          msg: 'Account and password are incorrect.'
        }
      }

      return {
        code: 0,
        msg: '登录成功',
        data: token
      }
    }
  },

  // get user info
  {
    url: '/user/info\.*',
    type: 'get',
    response: config => {
      const { token } = config.query
      const info = users[token]

      // mock error
      if (!info) {
        return {
          code: 50008,
          msg: 'Login failed, unable to get user details.'
        }
      }

      return {
        code: 0,
        msg: '获取成功',
        data: info
      }
    }
  },

  // user list
  {
    url: '/user/list',
    type: 'get',
    response: _ => {
      return {
        code: 0,
        data: [{
          account: 'admin',
          nickname: '超级管理员',
          roles: ['admin'],
          createtime: new Date()
        },
        {
          account: 'editor',
          nickname: '普通编辑',
          roles: ['editor'],
          createtime: new Date()
        },
        {
          account: 'user11',
          nickname: '普通用户',
          roles: ['user'],
          createtime: new Date()
        }]
      }
    }
  }
]
