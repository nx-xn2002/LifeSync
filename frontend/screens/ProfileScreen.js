import {Pressable, StyleSheet, Text, View} from "react-native";
import {AuthContext} from "../context/AuthContext";
import {useContext} from "react";

export default function ProfileScreen({navigation}) {
    const {user, clearUser} = useContext(AuthContext);
    const userLogout = () => {
        clearUser();
        navigation.navigate('Login');
    }
    return (
        <View style={styles.container}>
            <Text style={styles.text}>Profile Screen</Text>
            {
                user ?
                    (
                        <View>
                            <View style={styles.infoCard}>
                                <Text style={styles.name}>{user.username}</Text>
                                <Text style={styles.hp}>password:{user.password}</Text>
                            </View>
                        </View>
                    ) : (
                        <View style={styles.infoCard}>
                            <Text style={styles.name}>访客账号</Text>
                        </View>
                    )
            }
            <Pressable onPress={userLogout}>
                <View style={styles.infoCard}>
                    <Text>{user ? "切换账号" : "登录"}</Text>
                </View>
            </Pressable>
        </View>
    );
}
const styles = StyleSheet.create({
    container: {
        flex: 1,
        backgroundColor: '#f5f5f5',
    }, text: {
        fontSize: 20,
        fontWeight: 'bold',
        marginBottom: 20,
    }, infoCard: {
        backgroundColor: 'white',
        borderRadius: 16,
        borderWidth: 2,
        padding: 16,
        margin: 16,
        elevation: 35,
    }, name: {
        fontSize: 30,
        fontWeight: 'bold',
    }, hp: {
        fontSize: 22,
    },
});