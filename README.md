подключить

добавить в pom.xml
```
    <repositories>
        <repository>
            <id>jitpack.io</id>
            <url>https://jitpack.io</url>
        </repository>
    </repositories>

   <dependency>
         <groupId>com.github.NikolayNN</groupId>
         <artifactId>kafka-vars</artifactId>
         <version>TAG</version>
   </dependency>
```

Настроить KafkaAdmin используя KafkaAdminFactory

Настроить тему KafkaTopicFactory

### Строковые параметры сообщения

`MessageTransportable.strParams` — `Map<String, String>` для строковых параметров,
например `{"ble_1_id":"0012AB34CD56"}`. Идентификаторы сохраняются целиком,
включая ведущие нули. Числовые параметры по-прежнему передаются в строке `params`.

В новом конструкторе `strParams` передаётся сразу после `params`. Старый конструктор
сохранён и задаёт пустую карту. Отсутствующее или `null` значение при JSON-чтении
также становится пустой картой.

Avro-схема генерируется из модели: поле `strParams` имеет тип `map` со значениями
`string` и `default: {}`. Default применяется при разрешении схем Avro с известными
схемами записи и чтения. Текущий бинарный сериализатор не передаёт схему записи,
поэтому обновление producer и consumer нужно согласовать; один default не позволяет
читать старые бинарные сообщения десериализатором, настроенным только на новую схему.
