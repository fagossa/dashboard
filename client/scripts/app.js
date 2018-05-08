import Vue from 'vue';
import { TodoList } from './components/TodoList';
import { Speaker } from './components/Speaker';
import { Play } from './components/theatre/Actor';

const appElement = '#app';

new Vue({
  el: appElement,
});

const theatreElement = '#theatre';

new Vue({
  el: theatreElement,
  data: {
    romeoEtJulietteURL: '/theatre/romeoEtJuliette',
    ssu: new SpeechSynthesisUtterance(),
    romeoSpeaks: function(text, onend) {
      console.log("app.js :: romeoSpeaks");
      this.ssu.text = text;
      console.log(text);
      speechSynthesis.cancel();
      speechSynthesis.speak(this.ssu);
    },
    unknownSpeaks: () => {
      console.log("app.js :: unknownSpeaks");
      this.ssu.text = 'Qui parle ?';
      window.speechSynthesis.speak(this.ssu);
    },
  },
  methods: {
    readStream: function() {
      console.log("app.js :: readStream");

      const connection = new WebSocket('ws://localhost:8080/theatre/romeoEtJuliette');
      window.speechSynthesis.cancel()
      this.ssu.onend = () => connection.send('Ping');

      connection.onopen = () => connection.send('Ping');
      connection.onerror = (error) => console.error('WebSocket Error ', error);

      connection.onmessage = (event) => {
        const data = JSON.parse(event.data);
        if(data["name"] === "Romeo") this.romeoSpeaks(data.text);
        else this.unknownSpeaks();
      };
    },
  }
})
