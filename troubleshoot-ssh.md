# SSH Connection Troubleshooting Guide

## Common Causes of SSH Handshake Failures:

### 1. VPS-side Issues:
```bash
# Check SSH service status
sudo systemctl status ssh
sudo systemctl status sshd

# Check if SSH is listening on port 2222
sudo netstat -tlnp | grep :2222
sudo ss -tlnp | grep :2222

# Check SSH configuration
sudo nano /etc/ssh/sshd_config
# Ensure these settings:
# Port 2222
# PermitRootLogin no (if using non-root user)
# PubkeyAuthentication yes
# PasswordAuthentication no

# Restart SSH service after changes
sudo systemctl restart ssh
```

### 2. Firewall Issues:
```bash
# Check UFW status
sudo ufw status

# Allow SSH on port 2222
sudo ufw allow 2222/tcp

# Check iptables rules
sudo iptables -L -n
```

### 3. Network/VPS Provider Issues:
- Check if your VPS provider has rate limiting
- Verify your VPS isn't under DDoS protection that blocks GitHub Actions IPs
- Contact VPS provider to whitelist GitHub Actions IP ranges

### 4. SSH Key Issues:
```bash
# On VPS, check authorized_keys
cat ~/.ssh/authorized_keys

# Ensure correct permissions
chmod 700 ~/.ssh
chmod 600 ~/.ssh/authorized_keys
```

### 5. Resource Constraints:
```bash
# Check system resources
free -h
df -h
ps aux | head -20

# Check system logs for SSH issues
sudo journalctl -u ssh -f
sudo tail -f /var/log/auth.log
```

## Testing SSH Connection Manually:
```bash
# From your local machine, test SSH connection
ssh -p 2222 -v username@your-vps-ip

# Test from GitHub Actions runner (add this step temporarily):
- name: Debug SSH
  run: |
    ssh-keyscan -p 2222 ${{ secrets.VPS_IP }} || true
    echo "Testing connection..."
```

## Alternative Solutions:
1. Use RSYNC instead of SCP
2. Use different SSH port (22, 22222)
3. Use password authentication temporarily for testing
4. Use different deployment method (Docker registry, webhooks)
