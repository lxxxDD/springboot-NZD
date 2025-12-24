document.getElementById('fillBtn').addEventListener('click', () => {
  chrome.tabs.query({active: true, currentWindow: true}, (tabs) => {
    chrome.tabs.sendMessage(tabs[0].id, {action: "autofill"}, (response) => {
      if (chrome.runtime.lastError) {
        console.log("Error sending message:", chrome.runtime.lastError.message);
      } else {
        console.log("Response:", response);
      }
    });
  });
});
