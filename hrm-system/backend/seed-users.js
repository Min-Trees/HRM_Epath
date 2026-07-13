/**
 * Seed users for HRM dev environment via direct psql UPDATE.
 * Run AFTER backend has created the tables (Flyway migrated).
 *
 * Usage: node seed-users.js
 */
const crypto = require('crypto');

const PASSWORD = '123456';
const SALT = 'DevSeedSalt2026!'; // Fixed salt for reproducible hashes

function encodeFixed(rawPassword, saltStr) {
  const salt = Buffer.from(saltStr, 'utf8');
  const hash = crypto.createHash('sha256').update(salt).update(Buffer.from(rawPassword, 'utf8')).digest();
  return Buffer.from(salt).toString('base64') + ':' + Buffer.from(hash).toString('base64');
}

const hash = encodeFixed(PASSWORD, SALT);

const users = [
  'a.nguyen', 'b.tran', 'c.le', 'd.pham', 'e.hoang'
];

const { execSync } = require('child_process');

console.log(`Hash for "${PASSWORD}": ${hash}`);
console.log('');

for (const username of users) {
  try {
    const out = execSync(
      `psql -U postgres -h localhost -d hrm -c "UPDATE system.user_account SET password_hash = '${hash}' WHERE username = '${username}';"`,
      { encoding: 'utf8', stdio: 'pipe' }
    );
    console.log(`Updated password for: ${username}`);
  } catch (e) {
    console.warn(`Failed to update ${username}: ${(e.stderr || e.message || '').slice(0, 100)}`);
  }
}

console.log('\nDone. Login with any of the above users and password: ' + PASSWORD);
