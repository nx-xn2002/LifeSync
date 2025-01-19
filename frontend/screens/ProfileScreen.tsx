import {Pressable, SafeAreaView, StyleSheet, Text, View} from "react-native";
import {useContext, useEffect} from "react";

import type {BottomTabNavigationHelpers} from "@react-navigation/bottom-tabs/src/types";
import {AuthContext} from "../context/AuthContext";

export default function ProfileScreen({navigation}: { navigation: BottomTabNavigationHelpers }) {
    const {user, storeUser} = useContext(AuthContext);
    const userLogout = () => {
        user.username = ''
        user.password = ''
        storeUser(user);
        navigation.navigate('Login');
    }
    useEffect(() => {
        console.log(user)
    }, []);
    return (
        <SafeAreaView style={styles.container}>
            {
                user.username !== '' ?
                    (
                        <>
                            <View style={styles.infoCard}>
                                <Text style={styles.title}>username</Text>
                                <Text style={styles.body}>{user.username}</Text>
                            </View>
                            <View style={styles.infoCard}>
                                <Text style={styles.title}>password</Text>
                                <Text style={styles.body}>{user.password}</Text>
                            </View>
                        </>
                    ) : (
                        <View style={styles.infoCard}>
                            <Text style={styles.title}>访客账号</Text>
                        </View>
                    )
            }
            <View style={styles.infoCard}>
                <Text style={styles.title}>height</Text>
                <Text style={styles.body}>{user.basicHealth.height !== 0 ? user.basicHealth.height : '未填写'}</Text>
            </View>
            <View style={styles.infoCard}>
                <Text style={styles.title}>weight</Text>
                <Text style={styles.body}>{user.basicHealth.weight !== 0 ? user.basicHealth.weight : '未填写'}</Text>
            </View>
            <View style={styles.infoCard}>
                <Text style={styles.title}>age</Text>
                <Text style={styles.body}>{user.basicHealth.age !== 0 ? user.basicHealth.age : '未填写'}</Text>
            </View>
            <View style={styles.infoCard}>
                <Text style={styles.title}>gender</Text>
                <Text style={styles.body}>{user.basicHealth.gender !== '' ? user.basicHealth.gender : '未填写'}</Text>
            </View>
            <Pressable onPress={userLogout}>
                <View style={styles.infoCard}>
                    <Text>{user ? "切换账号" : "登录"}</Text>
                </View>
            </Pressable>
        </SafeAreaView>
    )
        ;
}
const styles = StyleSheet.create({
    container: {
        flex: 1,
        backgroundColor: '#f5f5f5',
    }, infoCard: {
        backgroundColor: 'white',
        borderRadius: 16,
        borderWidth: 1.5,
        padding: 16,
        margin: 16,
        elevation: 35,
        flexDirection: 'row',
        justifyContent: 'space-between',
        marginBottom: 32
    }, title: {}, body: {
        color: 'gray'
    },
});