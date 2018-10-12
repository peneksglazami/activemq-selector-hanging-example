import org.apache.activemq.ActiveMQConnectionFactory
import javax.jms.Session

const val MESSAGE_COUNT: Int = 200; // Значение 200 выбрано, потому что pageSize по умолчанию равно 200.

fun main(args: Array<String>) {
    val cf = ActiveMQConnectionFactory("vm://localhost?broker.persistent=false")
    val conn = cf.createConnection();
    try {
        conn.start();
        val session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
        try {
            val queue = session.createQueue("dynamicQueues/test")
            val producer = session.createProducer(queue);

            for (i in 1..MESSAGE_COUNT) {
                val message = session.createTextMessage("Message A #$i");
                message.setStringProperty("type", "A");
                producer.send(message)
            }

            for (i in 1..MESSAGE_COUNT) {
                val message = session.createTextMessage("Message B #$i");
                message.setStringProperty("type", "B");
                producer.send(message)
            }

            val consumer = session.createConsumer(queue, "type='B'");
            consumer.setMessageListener { m -> println(m) }
            Thread.sleep(5000)
        } finally {
            session.close();
        }
    } finally {
        conn.close()
    }
}