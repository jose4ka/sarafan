//Индекс сообщения
function getIndex(list, id){
    for (var i = 0; i < list.length; i++){
        if (list[i].id === id){
            return i;
        }
    }

    return -1;
}

var messageApi = Vue.resource('/message{/id}');

//Компонент отвечающий за ввод и сохранения сообщения
Vue.component('message-form', {

//Получаем список сообщений и аттрибуты
    props: ['messages', 'messageAttr'],

    data: function(){
        return {
            text: '',
            id: ''
        }
    },

//Получаем атрибуты сообщения
    watch: {
        messageAttr: function(newVal, oldVal){
            this.text = newVal.text;
            this.id = newVal.id;
        }
    },

    template:
            '<div>'+
            '<input type="text" placeholder="Write something" v-model="text"/>'+
            '<input type="button" value="Save" @click="save"/>'+
            '</div>',

     methods: {

     //Метод сохраняющий сообщения на сервере
        save: function(){

        //Текст сообщения
            var message = { text: this.text };

            if(this.id){
            //Передаём ID сообщения для обновления, и обновлённый текст
                messageApi.update({id: this.id}, message).then(result =>
                    result.json().then(data =>{

                        var index = getIndex(this.messages, data.id);
                        this.messages.splice(index, 1, data);
                        this.text = ''
                        this.id = ''
                    })
                )
            }
            else{
            //Если сообщения нет, то создаём его на сервере
               messageApi.save({}, message).then(result =>
                result.json().then(data =>{
                    this.messages.push(data);
                    this.text = ''
                })
            )
            }


        }
     }

});

//Vue компонент отвечающий за строку с сообщением, за её текст, кнопки и т.д.
Vue.component('message-row',{

//Данные которые принимает этот компонент
props: ['message', 'editMethod', 'messages'],

//Идёт разметка
    template: '<div>'+
    '<i>({{ message.id}})</i>{{message.text}}'+
    '<span style="position: absolute; right: 0">'+
        '<input type="button" value="Edit" @click="edit" />'+
        '<input type="button" value="X" @click="del" />'+
    '</span>'+
    '</div>',

//Методы которые используются  в этом компоненте
    methods:{
    //Изменить сообщение
        edit: function(){
            this.editMethod(this.message);
        },

//Удалить сообщение
        del: function(){
            this.editMethod(
                messageApi.remove({id: this.message.id}).then(result => {
                    if(result.ok){
                        this.messages.splice(this.messages.indexOf(this.message), 1)
                    }
                })
            )
        }

    }
});

//Компонент со списком
Vue.component('messages-list', {

//В данном компоненте мы принимаем только сообщения для отображения
  props: ['messages'],

  data: function(){
    return{
        message: null
    }
  },

  //Разметка компонента
  template:
  '<div style="position: relative; width: 300px;">'+
    '<message-form :messages="messages" :messageAttr="message"/>'+
    '<message-row v-for="message in messages" :key="message.id" :message="message" :editMethod="editMethod" :messages="messages"/>'+
  '</div>',


  methods: {
    editMethod: function(message){
        this.message = message;
    }
  }

})

var app = new Vue({
        el: '#app',
        template:
        '<div>'+
            '<div v-if="!profile">Необходимо авторизоваться через <a href="/login">Google</a></div>'+//Выводим если юзер не авторизован
            '<div v-else>'+//Иначе, если юзер авторизован, показываем кнопку выхода и список сообщений
                '<div>{{profile.name}}&nbsp;<a href="/logout">Выйти</a></div>'+
                '<messages-list  :messages = "messages" />'+
            '</div>'+
        '</div>',
        //Дата с самими сообщениями, и пользователем
        data: {
            messages: frontendData.messages,
            profile: frontendData.profile
        },
        created: function(){
//             messageApi.get().then(result =>
//                    result.json().then(data =>
//                        data.forEach(message => this.messages.push(message))
//                    )
//             )
          }
    })