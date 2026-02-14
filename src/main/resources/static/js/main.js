let stompClient = null;
let username = null;

const usernamePage = document.querySelector('#username-page');
const chatPage = document.querySelector('#chat-page');
const usernameForm = document.querySelector('#usernameForm');
const messageForm = document.querySelector('#messageForm');
const nameInput = document.querySelector('#name');
const messageInput = document.querySelector('#message');
const messageArea = document.querySelector('#messageArea');
const connectingElement = document.querySelector('.connecting');

document.addEventListener('DOMContentLoaded', function () {
    if (usernameForm) usernameForm.addEventListener('submit', connect, true);
    if (messageForm) messageForm.addEventListener('submit', sendMessage, true);
});

function connect(event) {
    username = nameInput.value.trim();

    if (username) {
        usernamePage.classList.add('hidden');
        chatPage.classList.remove('hidden');

        const endpoint = '/chat';

        var socket = new SockJS(endpoint);
        stompClient = Stomp.over(socket);


        stompClient.connect({}, onConnected, onError);
    } else {
        nameInput.focus();
    }
    event.preventDefault();
}

function onConnected() {
    connectingElement.classList.add('hidden');

    const publicTopic = '/topic/public';
    stompClient.subscribe(publicTopic, onMessageReceived);

    stompClient.send("/app/addUser", {}, JSON.stringify({ sender: username, messageType: 'JOIN' }));
}

function onError(error) {
    connectingElement.textContent = 'Could not connect to WebSocket server. Please refresh this page to try again!';
    connectingElement.style.color = 'red';
}

function sendMessage(event) {
    event.preventDefault();
    const messageContent = messageInput.value.trim();
    if (messageContent && stompClient) {
        var chatMessage = {
            sessionId: "",
            sender: username,
            content: messageContent,
            messageType: 'CHAT'
        };
        stompClient.send("/app/sendMessage", {}, JSON.stringify(chatMessage));
        messageInput.value = '';
    }
}

function onMessageReceived(payload) {
    try {
        var message = JSON.parse(payload.body);
    } catch (e) {
        return;
    }

    const messageElement = document.createElement('li');
    const bubble = document.createElement('div');
    bubble.classList.add('message');
    bubble.classList.add(message.sender === username ? 'self' : 'other');

    const senderEl = document.createElement('div');
    senderEl.classList.add('sender');
    senderEl.textContent = message.sender || 'Anonymous';

    const textEl = document.createElement('div');
    textEl.classList.add('text');
    textEl.textContent = message.content || '';

    bubble.appendChild(senderEl);
    bubble.appendChild(textEl);
    messageElement.appendChild(bubble);
    messageArea.appendChild(messageElement);

    messageArea.scrollTop = messageArea.scrollHeight;
}
