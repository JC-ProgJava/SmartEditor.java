var fontColor = "black";
document.addEventListener('DOMContentLoaded', setATimeout());
document.addEventListener('contextmenu',  event => event.preventDefault());
function theme() {
   document.body.classList.toggle("dark-mode");
   if(fontColor === "black"){
      fontColor = "white";
      localStorage.setItem('Theme','white');
   }else {
      fontColor = "black";
      localStorage.setItem('Theme','black');
   }
}

function setATimeout(){
    setTimeout(function () {

        if(localStorage.getItem('Theme') === "white"){
            theme();
        }
    }, 1500);
}
