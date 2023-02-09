import React, {Component} from 'react';
import {StyleSheet, Text, View, TextInput} from 'react-native';
import {Link} from 'react-router-native';

export default class Start extends Component {
  state = {
    firstName: null,
    lastName: null,
  };

  onChangeText = (key, value) => {
    this.setState({
      [key]: value,
    });
  };

  render() {
    return (
      <View style={styles.container}>
        <Text style={styles.label}>First Name</Text>
        <TextInput
          style={styles.input}
          onChangeText={text => this.onChangeText('firstName', text)}
          value={this.state.firstName}
        />
        <Text style={styles.label}>Last Name</Text>
        <TextInput
          style={styles.input}
          onChangeText={text => this.onChangeText('lastName', text)}
          value={this.state.lastName}
        />
        <Link
          style={styles.button}
          to={`/sdk/${this.state.firstName}/${this.state.lastName}`}>
          <Text style={styles.buttonText}>Start SDK</Text>
        </Link>
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
  buttonText: {
    fontSize: 20,
    textAlign: 'center',
    margin: 10,
    color: '#FFFFFF',
  },
  button: {
    backgroundColor: '#0080FF',
    marginBottom: 10,
    borderRadius: 10,
    marginTop: 20,
  },
  input: {
    borderColor: 'gray',
    borderWidth: 1,
    height: 40,
    minWidth: '70%',
  },
  label: {
    fontSize: 18,
    textAlign: 'center',
    alignSelf: 'flex-start',
    marginTop: 10,
    marginBottom: 5,
  },
});
