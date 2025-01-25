import {StatusBar, StyleSheet, Text, View} from "react-native";
import type {BottomTabNavigationHelpers} from "@react-navigation/bottom-tabs/src/types";
import {Button} from "@/components/ui";

export default function MonitorScreen({navigation}: { navigation: BottomTabNavigationHelpers }) {
    return (
        <View style={styles.container}>
            <Text style={styles.text}>Monitor Screen</Text>
            <Button onPress={() => navigation.navigate('Login')}/>
        </View>
    );
}
const styles = StyleSheet.create({
    container: {
        flex: 1,
        alignItems: 'center',
        justifyContent: 'center',
        paddingTop: StatusBar.currentHeight,
    }, text: {
        fontSize: 20,
        fontWeight: 'bold',
        marginBottom: 20,
    },
});