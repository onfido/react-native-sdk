import React from 'react';
import {StyleSheet, Text, View} from 'react-native';
import {Link} from 'react-router-native';

export default props => {
  return (
    <View style={styles.container}>
      <Text style={styles.welcome}>Status:</Text>
      <Text style={styles.instructions}>{props.match.params.status}</Text>
      <Text style={styles.welcome}>Response:</Text>
      <Text style={styles.instructions}>{props.match.params.message}</Text>
      <Link style={styles.button} to="/">
        <Text style={styles.buttonText}>Restart SDK</Text>
      </Link>
    </View>
  );
};

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
