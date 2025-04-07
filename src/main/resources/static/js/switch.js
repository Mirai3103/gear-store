let wrapper = document.querySelector(".wrapper");
let signup_links = document.querySelectorAll(".signup-link"); // Select all .signup-link elements
let login_link = document.querySelector(".login-link");

// Loop through all signup links and apply the event listener
signup_links.forEach(link => {
  link.addEventListener("click", () => {
    wrapper.classList.add("active");
  });
});

login_link.addEventListener("click", () => {
  wrapper.classList.remove("active");
});
//  default is login page, but if has query string "signup" then show signup page
if (window.location.search.includes("signup")||isRegister) {
  wrapper.classList.add("active");
}