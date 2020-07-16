import React, {Component} from 'react';
import {Button, StyleSheet, Text, View, Platform} from 'react-native';
import {Onfido, OnfidoCaptureType} from '@onfido/react-native-sdk';
import {Redirect} from 'react-router-native';
import createSdkToken from './backend-server-example';

export default class App extends Component {
  state = {
    title: 'Welcome to Onfido React Native SDK!',
    subtitle: "To get started, press 'Launch'",
    status: 'Starting',
    message: '--',
    sdkToken: null,
    sdkFlowComplete: false,
  };

  componentDidMount() {
    // In your app, you will need to gather the user's information beforehand for the sdk to work. Only first and last name are required.
    const propsMatch = this.props.match ? this.props.match : {params: {}};
    const {firstName, lastName} = propsMatch.params;
    // Your application id may be different on iOS and Android so you'll need to check which platform the code is running on first.
    const applicationId =
      Platform.OS === 'ios'
        ? 'org.reactjs.native.example.SampleApp'
        : 'com.sampleapp';
    const applicant = {
      first_name: !firstName || firstName.trim() === '' ? 'Jane' : firstName,
      last_name: !lastName || lastName.trim() === '' ? 'Doe' : lastName,
    };
    this.getSDKToken(applicant, applicationId);
  }

  getSDKToken = async (applicant, applicationId) => {
    const newState = await createSdkToken(applicant, applicationId);
    this.setState(newState);
  };

  startSDK = () => {
    Onfido.start({
      sdkToken: this.state.sdkToken,
      localisation: {
        ios_strings_file_name: 'Localizable',
      },
      flowSteps: {
        welcome: true,
        captureDocument: {},
        captureFace: {
          type: OnfidoCaptureType.VIDEO,
        },
      },
    })
      .then(response => {
        this.setState({
          status: 'resolved',
          message: JSON.stringify(response),
          sdkFlowComplete: true,
        });
      })
      .catch(error => {
        this.setState({
          status: 'rejected',
          message: error.code + ': ' + error.message,
          sdkFlowComplete: true,
        });
      });
  };

  render() {
    return (
      <View style={styles.container}>
        <Text style={styles.welcome}>{this.state.title}</Text>
        <Text style={styles.instructions}>{this.state.subtitle}</Text>
        <Button title="Launch" onPress={() => this.startSDK()} />
        {this.state.sdkFlowComplete ? (
          <Redirect to={`/finish/${this.state.status}/${this.state.message}`} />
        ) : null}
      </View>
    );
  }
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: '#F5FCFF',
  },
  welcome: {
    fontSize: 20,
    textAlign: 'center',
    margin: 10,
  },
  instructions: {
    textAlign: 'center',
    color: '#333333',
    marginBottom: 10,
  },
});
