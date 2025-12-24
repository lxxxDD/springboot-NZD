// Helper function to generate random strings
function getRandomString(length) {
  const chars = 'abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ';
  let result = '';
  for (let i = 0; i < length; i++) {
    result += chars.charAt(Math.floor(Math.random() * chars.length));
  }
  return result;
}

function getRandomName() {
  const firstNames = ["James", "Mary", "John", "Patricia", "Robert", "Jennifer", "Michael", "Linda", "William", "Elizabeth"];
  const lastNames = ["Smith", "Johnson", "Williams", "Brown", "Jones", "Garcia", "Miller", "Davis", "Rodriguez", "Martinez"];
  return {
    first: firstNames[Math.floor(Math.random() * firstNames.length)],
    last: lastNames[Math.floor(Math.random() * lastNames.length)],
    middle: getRandomString(1).toUpperCase()
  };
}

// 50 random US addresses
const US_ADDRESSES = [
  { street: "123 Main St", city: "New York", state: "NY", zip: "10001" },
  { street: "456 Oak Ave", city: "Los Angeles", state: "CA", zip: "90001" },
  { street: "789 Pine Rd", city: "Chicago", state: "IL", zip: "60601" },
  { street: "321 Elm St", city: "Houston", state: "TX", zip: "77001" },
  { street: "654 Maple Dr", city: "Phoenix", state: "AZ", zip: "85001" },
  { street: "987 Cedar Ln", city: "Philadelphia", state: "PA", zip: "19101" },
  { street: "147 Birch Blvd", city: "San Antonio", state: "TX", zip: "78201" },
  { street: "258 Walnut Way", city: "San Diego", state: "CA", zip: "92101" },
  { street: "369 Cherry Ct", city: "Dallas", state: "TX", zip: "75201" },
  { street: "741 Spruce St", city: "San Jose", state: "CA", zip: "95101" },
  { street: "852 Ash Ave", city: "Austin", state: "TX", zip: "78701" },
  { street: "963 Willow Rd", city: "Jacksonville", state: "FL", zip: "32099" },
  { street: "159 Poplar Dr", city: "Fort Worth", state: "TX", zip: "76101" },
  { street: "357 Hickory Ln", city: "Columbus", state: "OH", zip: "43085" },
  { street: "468 Chestnut Blvd", city: "Charlotte", state: "NC", zip: "28201" },
  { street: "579 Sycamore Way", city: "Indianapolis", state: "IN", zip: "46201" },
  { street: "681 Magnolia Ct", city: "Seattle", state: "WA", zip: "98101" },
  { street: "792 Dogwood St", city: "Denver", state: "CO", zip: "80201" },
  { street: "813 Redwood Ave", city: "Boston", state: "MA", zip: "02101" },
  { street: "924 Palm Rd", city: "Nashville", state: "TN", zip: "37201" },
  { street: "135 Cypress Dr", city: "Detroit", state: "MI", zip: "48201" },
  { street: "246 Juniper Ln", city: "Portland", state: "OR", zip: "97201" },
  { street: "357 Fir Blvd", city: "Memphis", state: "TN", zip: "38101" },
  { street: "468 Hemlock Way", city: "Oklahoma City", state: "OK", zip: "73101" },
  { street: "579 Sequoia Ct", city: "Las Vegas", state: "NV", zip: "89101" },
  { street: "690 Cottonwood St", city: "Louisville", state: "KY", zip: "40201" },
  { street: "701 Pecan Ave", city: "Baltimore", state: "MD", zip: "21201" },
  { street: "812 Beech Rd", city: "Milwaukee", state: "WI", zip: "53201" },
  { street: "923 Olive Dr", city: "Albuquerque", state: "NM", zip: "87101" },
  { street: "134 Laurel Ln", city: "Tucson", state: "AZ", zip: "85701" },
  { street: "245 Holly Blvd", city: "Fresno", state: "CA", zip: "93701" },
  { street: "356 Ivy Way", city: "Sacramento", state: "CA", zip: "95801" },
  { street: "467 Hazel Ct", city: "Kansas City", state: "MO", zip: "64101" },
  { street: "578 Alder St", city: "Mesa", state: "AZ", zip: "85201" },
  { street: "689 Linden Ave", city: "Atlanta", state: "GA", zip: "30301" },
  { street: "790 Aspen Rd", city: "Omaha", state: "NE", zip: "68101" },
  { street: "801 Locust Dr", city: "Colorado Springs", state: "CO", zip: "80901" },
  { street: "912 Mulberry Ln", city: "Raleigh", state: "NC", zip: "27601" },
  { street: "123 Catalpa Blvd", city: "Miami", state: "FL", zip: "33101" },
  { street: "234 Basswood Way", city: "Cleveland", state: "OH", zip: "44101" },
  { street: "345 Sweetgum Ct", city: "Tulsa", state: "OK", zip: "74101" },
  { street: "456 Redbud St", city: "Oakland", state: "CA", zip: "94601" },
  { street: "567 Buckeye Ave", city: "Minneapolis", state: "MN", zip: "55401" },
  { street: "678 Hackberry Rd", city: "Wichita", state: "KS", zip: "67201" },
  { street: "789 Serviceberry Dr", city: "Arlington", state: "TX", zip: "76001" },
  { street: "890 Hornbeam Ln", city: "New Orleans", state: "LA", zip: "70112" },
  { street: "901 Pawpaw Blvd", city: "Bakersfield", state: "CA", zip: "93301" },
  { street: "112 Tupelo Way", city: "Tampa", state: "FL", zip: "33601" },
  { street: "223 Osage Ct", city: "Aurora", state: "CO", zip: "80010" },
  { street: "334 Persimmon St", city: "Anaheim", state: "CA", zip: "92801" }
];

function getRandomAddress() {
  return US_ADDRESSES[Math.floor(Math.random() * US_ADDRESSES.length)];
}

function getRandomEmail() {
  const domains = ["lxxxsmt.icu", "996211958.xyz"];
  const randomStr = getRandomString(8).toLowerCase();
  const randomNum = Math.floor(Math.random() * 10000);
  const domain = domains[Math.floor(Math.random() * domains.length)];
  return `${randomStr}${randomNum}@${domain}`;
}

function getRandomDate(ageApprox) {
  const currentYear = new Date().getFullYear();
  const birthYear = currentYear - ageApprox;
  const month = Math.floor(Math.random() * 12) + 1;
  const day = Math.floor(Math.random() * 28) + 1;
  const mm = month.toString().padStart(2, '0');
  const dd = day.toString().padStart(2, '0');
  return `${mm}/${dd}/${birthYear}`;
}

function fillMatSelect(selector) {
  const select = document.querySelector(selector);
  if (select) {
    select.click();
    setTimeout(() => {
      const options = document.querySelectorAll('mat-option');
      if (options.length > 0) {
        const randomIndex = Math.floor(Math.random() * options.length);
        options[randomIndex].click();
      }
    }, 500);
    return true;
  }
  return false;
}

// Function to simulate typing into an input field
function fillInput(selector, value) {
  const input = document.querySelector(selector);
  if (input) {
    input.focus();
    input.value = value;
    input.dispatchEvent(new Event('input', { bubbles: true }));
    input.dispatchEvent(new Event('change', { bubbles: true }));
    input.dispatchEvent(new Event('blur', { bubbles: true }));
    console.log(`Filled ${selector} with ${value}`);
    return true;
  }
  return false;
}

// Main function to auto-fill the form
function autoFill() {
  console.log("Auto-filling form...");

  // Step 1: Name
  if (document.querySelector('input[formcontrolname="firstName"]')) {
    const name = getRandomName();
    fillInput('input[formcontrolname="firstName"]', name.first);
    fillInput('input[formcontrolname="middleName"]', name.middle);
    fillInput('input[formcontrolname="lastName"]', name.last);
  }

  // Step 2: DOB & Gender
  if (document.querySelector('input[formcontrolname="dob"]')) {
    const dob = getRandomDate(20);
    fillInput('input[formcontrolname="dob"]', dob);
    fillMatSelect('mat-select[formcontrolname="gender"]');
  }

  // Step 4: Contact Details (Email, Address)
  if (document.querySelector('input[formcontrolname="email"]')) {
    const email = getRandomEmail();
    const addr = getRandomAddress();
    
    // Fill email
    fillInput('input[formcontrolname="email"]', email);
    
    // Fill address fields
    fillInput('input[formcontrolname="add1"]', addr.street);
    fillInput('input[formcontrolname="city"]', addr.city);
    fillInput('input[formcontrolname="zip"]', addr.zip);
    
    // Fill state (native select element)
    const stateSelect = document.querySelector('select[formcontrolname="region"]');
    if (stateSelect) {
      stateSelect.value = addr.state;
      stateSelect.dispatchEvent(new Event('change', { bubbles: true }));
      console.log(`Selected state: ${addr.state}`);
    }
  }

  // Click Next Button
  setTimeout(() => {
    const nextBtn = document.querySelector('button[data-cy="next"]');
    if (nextBtn) {
      console.log("Clicking Next button...");
      nextBtn.click();
    }
  }, 1500);
}

// Function to save account details on completion page
function saveAccountDetails() {
  const nameEl = document.querySelector('[data-cy="created-name"]');
  const userIdEl = document.querySelector('[data-cy="created-username"]');
  const psuIdEl = document.querySelector('[data-cy="created-psuid"]');
  
  if (nameEl && userIdEl && psuIdEl) {
    const accountData = {
      name: nameEl.textContent.trim(),
      userId: userIdEl.textContent.trim(),
      pennStateId: psuIdEl.textContent.trim(),
      email: userIdEl.textContent.trim() + "@psu.edu",
      createdAt: new Date().toISOString()
    };
    
    console.log("Account created:", accountData);
    
    // Download as JSON file
    const blob = new Blob([JSON.stringify(accountData, null, 2)], { type: 'application/json' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `psu_account_${accountData.userId}.json`;
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
    URL.revokeObjectURL(url);
    
    return accountData;
  }
  return null;
}

// Auto-detect completion page and save
if (window.location.href.includes('/create/complete')) {
  setTimeout(() => {
    saveAccountDetails();
  }, 1000);
}

// Listen for messages from the popup
chrome.runtime.onMessage.addListener((request, sender, sendResponse) => {
  if (request.action === "autofill") {
    autoFill();
    sendResponse({status: "done"});
  }
  if (request.action === "saveAccount") {
    const data = saveAccountDetails();
    sendResponse({status: "done", data: data});
  }
});
