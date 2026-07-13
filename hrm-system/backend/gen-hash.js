/**
 * Generate fixed password hashes (salt = "FixedSaltForDev12")
 * matching com.company.hrm.system.security.PasswordEncoder
 */
const crypto = require('crypto');

const SALT_HEX = '466978656453616c74466f724465763132'; // "FixedSaltForDev12" as hex

function encodeFixed(rawPassword) {
  const salt = Buffer.from(SALT_HEX, 'hex');
  const hash = crypto.createHash('sha256').update(salt).update(rawPassword).digest();
  return Buffer.from(salt).toString('base64') + ':' + Buffer.from(hash).toString('base64');
}

const password = process.argv[2] || '123456';
console.log(encodeFixed(password));
