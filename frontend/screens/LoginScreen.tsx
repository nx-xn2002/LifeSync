import {
    Button,
    KeyboardAvoidingView,
    Platform,
    SafeAreaView,
    StatusBar,
    StyleSheet,
    Text,
    TextInput,
    View
} from 'react-native';
import {useContext, useState} from "react";
import type {BottomTabNavigationHelpers} from "@react-navigation/bottom-tabs/src/types";
import {AuthContext, AuthProvider} from "../context/AuthContext";
import {login} from "../services/api";


export default function LoginScreen({navigation}: { navigation: BottomTabNavigationHelpers }) {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [errors, setErrors] = useState<ERROR.RegisterError>({});
    const {storeUser} = useContext(AuthContext);

    const validateForm = () => {
        setErrors({})
        if (!username) {
            errors.username = 'Username is required';
        }
        if (!password) {
            errors.password = 'Password is required';
        }
        setErrors(errors);
        return Object.keys(errors).length === 0;
    }

    const handleSubmit = async () => {
        if (validateForm()) {
            try {
                const response = await login({username, password});
                if (response.data) {
                    await storeUser(response.data);
                    setErrors({});
                    setUsername("");
                    setPassword("");
                    console.log('User logged in');
                    navigation.navigate('MainTabs');
                } else {
                    console.log('Login failed', response.message);
                }
            } catch (error) {
                console.log('Error storing user:', error);
            }
        } else {
            console.log('Form validation failed');
        }
    }

    return (
        <AuthProvider>
            <SafeAreaView style={styles.container}>
                <StatusBar/>
                <KeyboardAvoidingView behavior={'padding'} keyboardVerticalOffset={Platform.OS === 'ios' ? 80 : 0}>
                    <View style={styles.form}>
                        <Text style={styles.label}>Username</Text>
                        <TextInput
                            style={styles.input}
                            placeholder={'Enter your username'}
                            placeholderTextColor={'gray'}
                            value={username}
                            onChangeText={setUsername}
                        />
                        {errors.username && <Text style={styles.errorText}>{errors.username}</Text>}
                        <Text style={styles.label}>Password</Text>
                        <TextInput
                            style={styles.input}
                            placeholder={'Enter your password'}
                            placeholderTextColor={'gray'}
                            value={password}
                            onChangeText={setPassword}
                            secureTextEntry
                        />
                        {errors.password && <Text style={styles.errorText}>{errors.password}</Text>}
                        <Button title={'Login'} onPress={handleSubmit}/>
                    </View>
                </KeyboardAvoidingView>
            </SafeAreaView>
        </AuthProvider>
    );
}

const styles = StyleSheet.create({
    container: {
        flex: 1,
        backgroundColor: "#f5f5f5",
        paddingTop: StatusBar.currentHeight,
    },
    form: {
        backgroundColor: 'white',
        margin: 16,
        padding: 20,
        borderRadius: 10,
        shadowColor: 'black',
        shadowOffset: {width: 0, height: 2},
        shadowOpacity: 0.25,
        shadowRadius: 4,
        elevation: 5,
    },
    label: {
        fontSize: 16,
        marginBottom: 5,
        fontWeight: 'bold',
    },
    input: {
        height: 40,
        borderColor: '#ddd',
        borderWidth: 1,
        marginBottom: 15,
        padding: 10,
        borderRadius: 5,
    },
    button: {
        marginBottom: 20,
        padding: 10,
    },
    errorText: {
        color: 'red',
        marginBottom: 10,
    },
});
