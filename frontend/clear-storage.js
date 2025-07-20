// Script to clear any invalid localStorage data
// Run this in browser console if needed
console.log('Clearing localStorage...');
localStorage.removeItem('user');
localStorage.removeItem('token');
localStorage.removeItem('role');
console.log('localStorage cleared!');
