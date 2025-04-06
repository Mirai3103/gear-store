document.getElementById("file-input").addEventListener("change", function() {
    let fileLabel = document.getElementById("file-name-label");
    if (this.files.length > 0) {
        fileLabel.textContent = this.files[0].name; // Show first file name
    } else {
        fileLabel.textContent = "No file selected";
    }
});