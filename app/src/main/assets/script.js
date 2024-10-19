const toggleButton = document.getElementById('toggle-nav-bar');
const myNavBar = document.getElementById('myNavBar');

toggleButton.addEventListener('click', () => {
  if (myNavBar.style.display === 'none') {
    myNavBar.style.display = 'flex';
  } else {
    myNavBar.style.display = 'none';
  }
});