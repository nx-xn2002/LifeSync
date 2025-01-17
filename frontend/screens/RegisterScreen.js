import {
    Button,
    Image,
    KeyboardAvoidingView,
    SafeAreaView,
    StyleSheet,
    Text,
    TextInput,
    View,
    Platform
} from 'react-native';
import {useState} from "react";
import {StatusBar} from "expo-status-bar";

export default function RegisterScreen() {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [errors, setErrors] = useState({});

    const validateForm = () => {
        let errors = {};
        if (!username) {
            errors.username = 'Username is required';
        }
        if (!password) {
            errors.password = 'Password is required';
        }
        setErrors(errors);
        return Object.keys(errors).length === 0;
    }
    const handleSubmit = () => {
        if (validateForm()) {
            console.log('Submitted', username, password);
            setErrors({});
            setUsername("");
            setPassword("");
        }
    }
    return (
        <SafeAreaView style={styles.container}>
            <StatusBar/>
            <KeyboardAvoidingView
                behavior={'padding'}
                keyboardVerticalOffset={Platform.OS === 'ios' ? 80 : 0}
            >
                <View style={styles.form}>
                    <Text style={styles.label}>Username</Text>
                    <TextInput
                        style={styles.input}
                        placeholder={'Enter your username'}
                        placeholderTextColor={'gray'}
                        value={username}
                        onChangeText={setUsername}
                    />
                    {
                        errors.username ? <Text style={styles.errorText}>{errors.username}</Text> : null
                    }
                    <Text style={styles.label}>Password</Text>
                    <TextInput
                        style={styles.input}
                        placeholder={'Enter your password'}
                        placeholderTextColor={'gray'}
                        value={password}
                        onChangeText={setPassword}
                        secureTextEntry
                    />
                    {
                        errors.password ? <Text style={styles.errorText}>{errors.password}</Text> : null
                    }
                    <Button title={'Login'} onPress={() => {
                        handleSubmit()
                    }}/>
                </View>
            </KeyboardAvoidingView>
        </SafeAreaView>
    );
}

const styles = StyleSheet.create({
    container: {
        flex: 1,
        backgroundColor: '#f5f5f5',
        paddingTop: StatusBar.currentHeight,
        paddingHorizontal: 20,
        justifyContent: 'center',
    }, form: {
        backgroundColor: 'white',
        padding: 20,
        borderRadius: 10,
        shadowColor: 'black',
        shadowOffset: {width: 0, height: 2},
        shadowOpacity: 0.25,
        shadowRadius: 4,
        elevation: 5,
    }, label: {
        fontSize: 16,
        marginBottom: 5,
        fontWeight: 'bold',
    }, input: {
        height: 40,
        borderColor: '#ddd',
        borderWidth: 1,
        marginBottom: 15,
        padding: 10,
        borderRadius: 5,
    }, button: {
        marginBottom: 20,
        padding: 10,
    }, errorText: {
        color: 'red',
        marginBottom: 10,
    }
});
