import {Keyboard, SafeAreaView, StatusBar, StyleSheet} from 'react-native';
import {useContext, useState} from "react";
import type {BottomTabNavigationHelpers} from "@react-navigation/bottom-tabs/src/types";
import {AuthContext, AuthProvider} from "@/context/AuthContext";
import {Controller, useForm} from "react-hook-form";
import {z} from "zod";
import {Toast, ToastTitle, useToast} from "@/components/ui/toast";
import {
    ArrowLeftIcon,
    Button,
    ButtonIcon,
    ButtonText,
    EyeIcon,
    EyeOffIcon,
    FormControl,
    FormControlError,
    FormControlErrorIcon,
    FormControlErrorText,
    FormControlLabel,
    FormControlLabelText,
    Heading,
    HStack,
    Icon,
    Input,
    InputField,
    InputIcon,
    InputSlot,
    Link,
    LinkText,
    Pressable,
    Text,
    VStack
} from "@/components/ui";
import {zodResolver} from "@hookform/resolvers/zod";
import {AlertTriangle} from "lucide-react-native";
import {login} from "@/services/api";
import FontAwesome6 from '@expo/vector-icons/FontAwesome6';

const loginSchema = z.object({
    //email: z.string().min(1, "Email is required").email(),
    password: z.string().min(1, "Password is required"),
    username: z.string().min(1, "Username is required"),
});

type LoginSchemaType = z.infer<typeof loginSchema>;

///

export default function RegisterScreen({navigation}: { navigation: BottomTabNavigationHelpers }) {
    // const [username, setUsername] = useState('');
    // const [password, setPassword] = useState('');
    // const [errors, setErrors] = useState<ERROR.RegisterError>({});
    const {storeUser} = useContext(AuthContext);

    ///
    const {
        control,
        handleSubmit,
        reset,
        formState: {errors},
    } = useForm<LoginSchemaType>({
        resolver: zodResolver(loginSchema),
    });
    const toast = useToast();
    const [validated, setValidated] = useState({
        usernameValid: true,
        passwordValid: true,
    });

    const onSubmit = async (data: LoginSchemaType) => {
        setValidated({usernameValid: true, passwordValid: true});
        const response = await login(data);
        if (response.data) {
            reset();
            toast.show({
                placement: "top",
                render: ({id}) => {
                    return (
                        <Toast nativeID={id} variant="outline" action="success">
                            <ToastTitle>Logged in successfully!</ToastTitle>
                        </Toast>
                    );
                },
            });
            storeUser(response.data);
            navigation.navigate('MainTabs');
        } else {
            toast.show({
                placement: "top",
                render: ({id}) => {
                    return (
                        <Toast nativeID={id} variant="outline" action="error">
                            <ToastTitle>Login failed!{response.description}</ToastTitle>
                        </Toast>
                    );
                },
            });
        }
    };
    const [showPassword, setShowPassword] = useState(false);

    const handleState = () => {
        setShowPassword((showState) => {
            return !showState;
        });
    };
    const handleKeyPress = () => {
        Keyboard.dismiss();
        handleSubmit(onSubmit)();
    };

    return (
        <AuthProvider>
            <SafeAreaView style={styles.container}>
                <VStack className="max-w-[440px] w-full" space="md" style={{padding: 20}}>
                    {/*页面标头*/}
                    <VStack className="md:items-center" space="md">
                        <Pressable
                            onPress={() => {
                                navigation.reset({
                                    index: 0,
                                    routes: [{name: 'MainTabs', params: {screen: 'Profile'}}],
                                });
                            }}
                        >
                            <Icon
                                as={ArrowLeftIcon}
                                className="md:hidden text-background-800"
                                size="xl"
                            />
                        </Pressable>
                        <VStack>
                            <Heading className="md:text-center" size="3xl">
                                Sign up
                            </Heading>
                            <Text>Sign up and start using LifeSync</Text>
                        </VStack>
                    </VStack>
                    <VStack className="w-full">
                        <VStack space="xl" className="w-full">
                            {/*用户名输入框*/}
                            <FormControl
                                isInvalid={!!errors?.username || !validated.usernameValid}
                                className="w-full"
                            >
                                <FormControlLabel>
                                    <FormControlLabelText>Username</FormControlLabelText>
                                </FormControlLabel>
                                <Controller
                                    defaultValue=""
                                    name="username"
                                    control={control}
                                    rules={{
                                        validate: async (value) => {
                                            try {
                                                await loginSchema.parseAsync({email: value});
                                                return true;
                                            } catch (error: any) {
                                                return error.message;
                                            }
                                        },
                                    }}
                                    render={({field: {onChange, onBlur, value}}) => (
                                        <Input>
                                            <InputField
                                                placeholder="Enter username"
                                                value={value}
                                                onChangeText={onChange}
                                                onBlur={onBlur}
                                                onSubmitEditing={handleKeyPress}
                                                returnKeyType="done"
                                                style={{width: "100%", height: 50,}}
                                            />
                                        </Input>
                                    )}
                                />
                                <FormControlError>
                                    <FormControlErrorIcon as={AlertTriangle}/>
                                    <FormControlErrorText>
                                        {errors?.username?.message ||
                                            (!validated.usernameValid && "Email ID not found")}
                                    </FormControlErrorText>
                                </FormControlError>
                            </FormControl>
                            <FormControl
                                isInvalid={!!errors.password || !validated.passwordValid}
                                className="w-full"
                            >
                                <FormControlLabel>
                                    <FormControlLabelText>Password</FormControlLabelText>
                                </FormControlLabel>
                                <Controller
                                    defaultValue=""
                                    name="password"
                                    control={control}
                                    rules={{
                                        validate: async (value) => {
                                            try {
                                                await loginSchema.parseAsync({password: value});
                                                return true;
                                            } catch (error: any) {
                                                return error.message;
                                            }
                                        },
                                    }}
                                    render={({field: {onChange, onBlur, value}}) => (
                                        <Input>
                                            <InputField
                                                type={showPassword ? "text" : "password"}
                                                placeholder="Enter password"
                                                value={value}
                                                onChangeText={onChange}
                                                onBlur={onBlur}
                                                onSubmitEditing={handleKeyPress}
                                                returnKeyType="done"
                                                style={{width: "100%", height: 50,}}
                                            />
                                            <InputSlot onPress={handleState} className="pr-3" style={{marginRight: 10}}>
                                                <InputIcon as={showPassword ? EyeIcon : EyeOffIcon}/>
                                            </InputSlot>
                                        </Input>
                                    )}
                                />
                                <FormControlError>
                                    <FormControlErrorIcon as={AlertTriangle}/>
                                    <FormControlErrorText>
                                        {errors?.password?.message ||
                                            (!validated.passwordValid && "Password was incorrect")}
                                    </FormControlErrorText>
                                </FormControlError>
                            </FormControl>
                            <FormControl
                                isInvalid={!!errors.password || !validated.passwordValid}
                                className="w-full"
                            >
                                <FormControlLabel>
                                    <FormControlLabelText>Password</FormControlLabelText>
                                </FormControlLabel>
                                <Controller
                                    defaultValue=""
                                    name="password"
                                    control={control}
                                    rules={{
                                        validate: async (value) => {
                                            try {
                                                await loginSchema.parseAsync({password: value});
                                                return true;
                                            } catch (error: any) {
                                                return error.message;
                                            }
                                        },
                                    }}
                                    render={({field: {onChange, onBlur, value}}) => (
                                        <Input>
                                            <InputField
                                                type={showPassword ? "text" : "password"}
                                                placeholder="Enter password"
                                                value={value}
                                                onChangeText={onChange}
                                                onBlur={onBlur}
                                                onSubmitEditing={handleKeyPress}
                                                returnKeyType="done"
                                                style={{width: "100%", height: 50,}}
                                            />
                                            <InputSlot onPress={handleState} className="pr-3" style={{marginRight: 10}}>
                                                <InputIcon as={showPassword ? EyeIcon : EyeOffIcon}/>
                                            </InputSlot>
                                        </Input>
                                    )}
                                />
                                <FormControlError>
                                    <FormControlErrorIcon as={AlertTriangle}/>
                                    <FormControlErrorText>
                                        {errors?.password?.message ||
                                            (!validated.passwordValid && "Password was incorrect")}
                                    </FormControlErrorText>
                                </FormControlError>
                            </FormControl>
                            {/*忘记密码*/}
                            <HStack className="w-full right-1 ">
                                <Link href="/auth/forgot-password">
                                    <LinkText
                                        className="font-medium text-sm text-primary-700 group-hover/link:text-primary-600">
                                        Forgot Password?
                                    </LinkText>
                                </Link>
                            </HStack>
                        </VStack>
                        {/*注册按钮*/}
                        <VStack className="w-full my-7 " space="lg" style={{paddingTop: 20}}>
                            <Button className="w-full" onPress={handleSubmit(onSubmit)}>
                                <ButtonText className="font-medium">Log in</ButtonText>
                            </Button>
                            <Button
                                variant="outline"
                                action="secondary"
                                className="w-full gap-1"
                                onPress={() => {
                                    toast.show({
                                        placement: "top",
                                        render: ({id}) => {
                                            return (
                                                <Toast nativeID={id} variant="outline" action="success">
                                                    <ToastTitle>Logged in successfully!</ToastTitle>
                                                </Toast>
                                            );
                                        },
                                    });
                                    navigation.navigate('MainTabs');
                                }}
                            >
                                <ButtonText className="font-medium">
                                    Continue as Guest
                                </ButtonText>
                                <ButtonIcon as={() => <FontAwesome6 name="user-secret" size={24} color="black"/>}/>
                            </Button>
                        </VStack>
                        {/*注册按钮*/}
                        <HStack className="self-center" space="sm">
                            <Text size="md">Already have an account?</Text>
                            <Link onPress={() => {
                                navigation.navigate('Login')
                            }}>
                                <LinkText
                                    className="font-medium text-primary-700 group-hover/link:text-primary-600 group-hover/pressed:text-primary-700"
                                    size="md"
                                >
                                    Login
                                </LinkText>
                            </Link>
                        </HStack>
                    </VStack>
                </VStack>
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
