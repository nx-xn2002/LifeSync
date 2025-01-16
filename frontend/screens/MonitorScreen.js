import {StyleSheet, Text, View} from "react-native";

export default function MonitorScreen() {
    return (
        <View style={styles.container}>
            <Text style={styles.text}>Monitor Screen</Text>
        </View>
    );
}
const styles = StyleSheet.create({
    container: {
        flex: 1,
        alignItems: 'center',
        justifyContent: 'center',
    }, text: {
        fontSize: 20,
        fontWeight: 'bold',
        marginBottom: 20,
    },
});