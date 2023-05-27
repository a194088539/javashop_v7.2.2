const { distribution, liveVideo } = require('./index')

module.exports = {
	NODE_ENV: '"development"',
	ENV_CONFIG: '"dev"',
  DISTRIBUTION: distribution,
  LIVEVIDEO: liveVideo
}
