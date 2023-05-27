const { distribution, liveVideo } = require('./index')

module.exports = {
	NODE_ENV: '"production"',
	ENV_CONFIG: '"prod"',
  DISTRIBUTION: distribution,
  LIVEVIDEO: liveVideo
}
